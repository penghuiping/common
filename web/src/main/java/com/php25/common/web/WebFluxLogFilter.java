package com.php25.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * weflux 通用日志打印
 *
 * @author penghuiping
 * @date 2019/7/24 17:21
 */
public class WebFluxLogFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(WebFluxLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        ServerHttpResponse response = serverWebExchange.getResponse();
        String uri = request.getPath().pathWithinApplication().value();
        log.info("请求开始访问路径为:{}", uri);
        //成功
        return webFilterChain.filter(serverWebExchange).doFinally(signalType -> {
            log.info("请求结束访问路径为:{}", uri);
        });
    }
}
