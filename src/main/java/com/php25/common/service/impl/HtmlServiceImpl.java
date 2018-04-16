package com.php25.common.service.impl;

import com.php25.common.service.HtmlService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by penghuiping on 3/19/15.
 *
 * html网页处理相关的通用方法
 *
 */
@Component("htmlService")
public class HtmlServiceImpl implements HtmlService {
    public String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        return basePath;
    }
}
