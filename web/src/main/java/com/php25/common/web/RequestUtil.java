package com.php25.common.web;

import com.php25.common.core.dto.CurrentUser;
import com.php25.common.core.exception.Exceptions;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author penghuiping
 * @date 2020/3/24 14:17
 */
public class RequestUtil {
    private static final String CURRENT_USER = "current_user";

    private RequestUtil() {
    }

    public static String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    }

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    public static CurrentUser getCurrentUser() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Object object = requestAttributes.getAttribute(CURRENT_USER, 0);
        if (null == object) {
            throw Exceptions.throwIllegalStateException("无法获取当前登录用户");
        }
        return (CurrentUser) object;
    }

    /**
     * 设置当前登录用户
     *
     * @param currentUser 当前登录用户
     */
    public static void setCurrentUser(CurrentUser currentUser) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        requestAttributes.setAttribute(CURRENT_USER, currentUser, 0);
    }
}
