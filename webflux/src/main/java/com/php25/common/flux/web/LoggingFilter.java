package com.php25.common.flux.web;

import com.php25.common.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author penghuiping
 * @date 2021/10/26 21:44
 */
public class LoggingFilter extends AbstractRequestLoggingFilter {

    private final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        if (StringUtil.isNotBlank(request.getHeader("version"))) {
            sb.append("[").append("version:").append(request.getHeader("version")).append("]");
        }

        logger.info("开始访问:{}", sb);
        request.setAttribute("startTime", System.currentTimeMillis());
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        Long startTime = (Long) request.getAttribute("startTime");
        logger.info("结束访问:{},总耗时:{}ms", message, (System.currentTimeMillis() - startTime));
    }

}
