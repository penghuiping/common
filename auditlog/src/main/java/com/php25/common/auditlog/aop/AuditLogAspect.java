package com.php25.common.auditlog.aop;

import com.google.common.collect.Lists;
import com.php25.common.auditlog.dto.AuditLogContent;
import com.php25.common.auditlog.manager.AuditLogManager;
import com.php25.common.core.util.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * @author penghuiping
 * @date 2020/7/13 15:26
 */
@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogManager<AuditLogContent> auditLogManager;

    @Pointcut("@annotation(com.php25.common.auditlog.aop.AuditLog)")
    public void auditLogPointCut() {
    }

    @Around("auditLogPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = point.proceed();

        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();
        String className = point.getTarget().getClass().getName();
        Object traceId0 = auditLogManager.getTraceId(getRequestAttributes());
        Object preSpanId0 = auditLogManager.getPreSpanId(getRequestAttributes());
        String traceId = traceId0 == null ? "" : traceId0.toString();
        String preSpanId = preSpanId0 == null ? "" : preSpanId0.toString();


        String userId = auditLogManager.getUserId(getRequestAttributes());
        String params = JsonUtil.toJson(Lists.newArrayList(point.getArgs()));

        AuditLogContent auditLogContent = new AuditLogContent();
        auditLogContent.setClassName(className);
        auditLogContent.setMethodName(methodName);
        auditLogContent.setUserId(userId);
        auditLogContent.setParams(params);

        long endTime = System.currentTimeMillis();
        auditLogManager.log(traceId, preSpanId, startTime, endTime, auditLogContent);
        return obj;
    }


    private RequestAttributes getRequestAttributes() {
        return RequestContextHolder.getRequestAttributes();
    }
}
