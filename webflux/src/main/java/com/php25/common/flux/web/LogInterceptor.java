package com.php25.common.flux.web;

import com.php25.common.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
public class LogInterceptor implements AsyncHandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestURL().toString());
        if (StringUtil.isNotBlank(request.getHeader("version"))) {
            sb.append("[").append("version:").append(request.getHeader("version")).append("]");
        }

        logger.info("开始访问:" + sb);
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestURL().toString());
        if (StringUtil.isNotBlank(request.getHeader("version"))) {
            sb.append("[").append("version:").append(request.getHeader("version")).append("]");
        }

        logger.info("开始加载视图:" + sb);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute("startTime");
        logger.info("结束加载视图" + request.getRequestURL().toString() + ",总耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
