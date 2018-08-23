package com.php25.common.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author penghuiping
 * @date 2015-03-19
 * <p>
 * html网页处理相关的通用方法
 */
@Component("htmlService")
@ConditionalOnClass(HttpServletRequest.class)
public class HtmlServiceImpl implements HtmlService {
    @Override
    public String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        return basePath;
    }
}
