package com.php25.common.interceptor;

import com.php25.common.service.impl.HtmlServiceImpl;
import com.php25.common.util.ThreadLocalUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有功能的共用拦截器，主要是向modelandview中加入一些常用的基路径
 * Created by penghuiping on 3/17/15.
 */
@Component
public class CommonInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = Logger.getLogger(CommonInterceptor.class);

    @Resource
    HtmlServiceImpl htmlService;

    @Value("${base_assets_url:null}")
    private String base_assets_url;

    @Value("${base_assets_upload_url:null}")
    private String base_assets_upload_url;

    @Value("${base_url:null}")
    private String base_url;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("开始访问" + request.getRequestURL().toString());
        ThreadLocalUtil.set(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (null != modelAndView) {
            if (!"null".equals(base_url)) {
                modelAndView.getModelMap().addAttribute("ctx", base_url);
            } else {
                modelAndView.getModelMap().addAttribute("ctx", htmlService.getBasePath(request));
            }
            modelAndView.getModelMap().addAttribute("frontAssetsUrl", base_assets_url + "front/");
            modelAndView.getModelMap().addAttribute("adminAssetsUrl", base_assets_url + "admin/");
            modelAndView.getModelMap().addAttribute("apiAssetsUrl", base_assets_url + "api/");
            modelAndView.getModelMap().addAttribute("uploadAssetsUrl", base_assets_upload_url);
        }
        logger.info("开始加载视图" + request.getRequestURL().toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("结束加载视图" + request.getRequestURL().toString());
    }
}
