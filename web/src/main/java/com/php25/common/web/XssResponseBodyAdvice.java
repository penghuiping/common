package com.php25.common.web;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author penghuiping
 * @date 2021/7/22 14:03
 */
@SuppressWarnings("rawtypes")
public abstract class XssResponseBodyAdvice implements ResponseBodyAdvice {
    private static final Logger log = LoggerFactory.getLogger(XssResponseBodyAdvice.class);

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        String responseBody = JsonUtil.toJson(body);
        boolean isValid = Jsoup.isValid(responseBody, this.configWhiteList());
        if (!isValid) {
            throw Exceptions.throwBusinessException("B9999", "responseBody存在不安全的html内容");
        }
        return body;
    }

    /**
     * 配置白名单标签
     * <p>
     * 如: Whitelist.basicWithImages();
     *
     * @return 白名单标签
     */
    public abstract Safelist configWhiteList();
}
