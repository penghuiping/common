package com.php25.common.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by penghuiping on 3/19/15.
 * <p>
 * html网页处理相关的通用方法
 */
@Component("htmlService")
@ConditionalOnClass(HttpServletRequest.class)
public class HtmlServiceImpl implements HtmlService {
    public String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        return basePath;
    }
}
