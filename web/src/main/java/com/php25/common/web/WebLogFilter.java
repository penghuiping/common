package com.php25.common.web;


import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.mask.MaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/25 19:22
 */
public class WebLogFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(WebLogFilter.class);

    private static final MaskManager maskManager = new MaskManager();
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private List<String> excludeUriPatterns;

    public void setExcludeUriPatterns(String... excludeUriPatterns) {
        this.excludeUriPatterns = Lists.newArrayList(excludeUriPatterns);
    }

    public void setMaskPattern(String... maskPatterns) {
        for (int i = 0; i < maskPatterns.length; i++) {
            maskManager.addMaskPattern(maskPatterns[i]);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String contentType = request.getContentType();
        String uri = request.getRequestURI();
        //如果是excludeUri则直接略过
        for (int i = 0; i < excludeUriPatterns.size(); i++) {
            String pattern = excludeUriPatterns.get(i);
            boolean isExcluded = antPathMatcher.match(pattern, uri);
            if (isExcluded) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String method = request.getMethod();
        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equals(contentType)
                || MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)
                || MediaType.TEXT_PLAIN_VALUE.equals(contentType)
        ) {
            LogRequestWrapper contentCachingRequestWrapper = new LogRequestWrapper(request);
            ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            log.info("开始访问url:[{} {}]", method, uri);
            log.info("request params为:{}", JsonUtil.toJson(request.getParameterMap()));
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
            String respContentType = response.getContentType();
            String result = new String(contentCachingResponseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (MediaType.APPLICATION_JSON_VALUE.equals(respContentType)
                    || MediaType.APPLICATION_JSON_UTF8_VALUE.equals(respContentType)) {
                result = maskManager.maskMessage(result);
                log.info("response params为:{}", result);
            }
            response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } else {
            filterChain.doFilter(request, response);
        }
        log.info("结束访问url:[{} {}],总耗时{}ms", method, uri, System.currentTimeMillis() - start);
    }


    private static class LogRequestWrapper extends HttpServletRequestWrapper {

        public LogRequestWrapper(HttpServletRequest request) {
            super(request);
        }


        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream inputStream = super.getInputStream();
            StringBuilder content = new StringBuilder();
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream)) {
                ByteBuffer buff = ByteBuffer.allocate(256);
                while (true) {
                    buff.clear();
                    int count = readableByteChannel.read(buff);
                    if (count <= 0) {
                        break;
                    }
                    content.append(new String(buff.array(), 0, buff.position(), Charsets.ISO_8859_1));
                }
            } catch (IOException e) {
                log.error("IOException:", e);
                throw new RuntimeException(e);
            }

            String requestBody = new String(content.toString().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
            log.info("request body为:{}", requestBody);
            final ByteArrayInputStream bis = new ByteArrayInputStream(requestBody.getBytes(Charsets.UTF_8));
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return bis.read();
                }
            };
        }
    }
}
