package com.php25.common.auditlog.manager;

import org.springframework.web.context.request.RequestAttributes;

/**
 * @author penghuiping
 * @date 2020/7/13 16:20
 */
public interface AuditLogAssist {

    /**
     * 获取用户id
     *
     * @param requestAttributes http请求属性
     * @return userId
     */
    String getUserId(RequestAttributes requestAttributes);

    /**
     * traceId
     *
     * @param requestAttributes http请求属性
     * @return traceId
     */
    String getTraceId(RequestAttributes requestAttributes);

    /**
     * preSpanId
     *
     * @param requestAttributes http请求属性
     * @return 上一个spanId
     */
    String getPreSpanId(RequestAttributes requestAttributes);
}
