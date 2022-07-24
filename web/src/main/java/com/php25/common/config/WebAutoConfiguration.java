package com.php25.common.config;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.validation.util.ValidatorUtil;
import com.php25.common.web.WebLogFilter;
import com.php25.common.web.XssRequestBodyAdvice;
import com.php25.common.web.XssResponseBodyAdvice;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2022/7/24 22:25
 */
@ComponentScan(basePackages = {"com.php25.common.web", "com.php25.common.trace"})
public class WebAutoConfiguration extends WebMvcConfigurationSupport {
    private static final Logger log = LoggerFactory.getLogger(WebAutoConfiguration.class);

    @Bean
    FilterRegistrationBean<WebLogFilter> loggingFilter() {
        FilterRegistrationBean<WebLogFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        WebLogFilter webLogFilter = new WebLogFilter();
        webLogFilter.setMaskPattern("password.{1,2}:.{1,2}([A-Za-z0-9]{6,}).{1,2},");
        webLogFilter.setExcludeUriPatterns();
        filterFilterRegistrationBean.setFilter(webLogFilter);
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public RestTemplate restTemplate(
            @Value("${httpClient.maxIdleConnection:10}") Integer maxIdleConnection,
            @Value("${httpClient.connectionTimeout:5000}") Integer connectionTimeout,
            @Value("${httpClient.readTimeout:5000}") Integer readTimeout,
            @Value("${httpClient.writeTimeout:5000}") Integer writeTimeout
    ) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(maxIdleConnection, 5, TimeUnit.MINUTES)).build();
        OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        okHttp3ClientHttpRequestFactory.setConnectTimeout(connectionTimeout);
        okHttp3ClientHttpRequestFactory.setReadTimeout(readTimeout);
        okHttp3ClientHttpRequestFactory.setWriteTimeout(writeTimeout);
        RestTemplate restTemplate = new RestTemplate(okHttp3ClientHttpRequestFactory);
        restTemplate.setMessageConverters(Lists.newArrayList(new StringHttpMessageConverter(), new MappingJackson2HttpMessageConverter(JsonUtil.getObjectMapper())));
        restTemplate.setInterceptors(Lists.newArrayList((httpRequest, body, clientHttpRequestExecution) -> {
            HttpHeaders headers = httpRequest.getHeaders();
            log.info("headers:{}", headers);
            log.info("body:{}", new String(body, Charsets.UTF_8));
            return clientHttpRequestExecution.execute(httpRequest, body);
        }));
        return restTemplate;
    }

    @Bean
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(4);
        final NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();
        ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        retryPolicy.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) classifiable -> {
            if (classifiable.getCause() instanceof SocketTimeoutException) {
                return simpleRetryPolicy;
            }
            //不执行直接走 RecoveryCallback。recover（）
            return neverRetryPolicy;
        });
        retryTemplate.setRetryPolicy(retryPolicy);
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(1000);
        exponentialBackOffPolicy.setMultiplier(2);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        retryTemplate.setThrowLastExceptionOnExhausted(true);
        return retryTemplate;
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(JsonUtil.getObjectMapper()));
        converters.add(new StringHttpMessageConverter());
    }

    @Override
    protected Validator getValidator() {
        return new SpringValidatorAdapter(ValidatorUtil.getValidator());
    }

    @Override
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.createRequestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setRequestBodyAdvice(Lists.newArrayList(new XssRequestBodyAdvice() {
            private final Safelist whitelist = Safelist.simpleText();

            @Override
            public Safelist configWhiteList() {
                return this.whitelist;
            }
        }));
        requestMappingHandlerAdapter.setResponseBodyAdvice(Lists.newArrayList(new XssResponseBodyAdvice() {
            private final Safelist whitelist = Safelist.simpleText();

            @Override
            public Safelist configWhiteList() {
                return this.whitelist;
            }
        }));
        return requestMappingHandlerAdapter;
    }
}
