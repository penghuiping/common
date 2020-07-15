package com.php25.common.auditlog.manager;

import org.springframework.web.context.request.RequestAttributes;

/**
 * @author penghuiping
 * @date 2020/7/13 14:12
 */
public interface AuditLogManager<T> {

    /**
     * 记录日志
     *
     * @param traceId   请求链id
     * @param preSpanId 请求链中上一环节id
     * @param startTime 进入本环节时间
     * @param endTime   离开本环节时间
     * @param content   日志内容
     */
    void log(String traceId, String preSpanId, Long startTime, Long endTime, T content);


    /**
     * 生成全局唯一的traceId或者spanId
     *
     * @return 全局唯一id
     */
    String generateId();

    /**
     * 从requestAttributes中获取请求链id
     *
     * @param requestAttributes request属性
     * @return traceId
     */
    String getTraceId(RequestAttributes requestAttributes);

    /**
     * 从requestAttributes中获取环节id
     *
     * @param requestAttributes request属性
     * @return preSpan
     */
    String getPreSpanId(RequestAttributes requestAttributes);


    /**
     * 获取用户id
     *
     * @return userId
     */
    String getUserId(RequestAttributes requestAttributes);

}
