package com.php25.common.flux.web;

import com.php25.common.core.util.ThreadLocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有功能的共用拦截器，主要是向modelandview中加入一些常用的基路径
 *
 * @author penghuiping
 * @date 2015-03-17
 */
@Component
@ConditionalOnClass(HttpServletRequest.class)
public class CommonInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

    @Value("${base_assets_url:null}")
    private String baseAssetsUrl;

    @Value("${base_assets_upload_url:null}")
    private String baseAssetsUploadUrl;

    @Value("${base_url:null}")
    private String baseUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("开始访问" + request.getRequestURL().toString());
        ThreadLocalUtil.set(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (null != modelAndView) {
            if (!"null".equals(baseUrl)) {
                modelAndView.getModelMap().addAttribute("ctx", baseUrl);
            } else {
                modelAndView.getModelMap().addAttribute("ctx", RequestUtil.getBasePath(request));
            }
            modelAndView.getModelMap().addAttribute("frontAssetsUrl", baseAssetsUrl + "front/");
            modelAndView.getModelMap().addAttribute("adminAssetsUrl", baseAssetsUrl + "admin/");
            modelAndView.getModelMap().addAttribute("apiAssetsUrl", baseAssetsUrl + "api/");
            modelAndView.getModelMap().addAttribute("uploadAssetsUrl", baseAssetsUploadUrl);
        }
        logger.info("开始加载视图" + request.getRequestURL().toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("结束加载视图" + request.getRequestURL().toString());
        ThreadLocalUtil.remove();
    }
}
