package com.php25.common.flux.trace.aop;

import brave.ScopedSpan;
import brave.Tracer;
import com.php25.common.core.util.StringUtil;
import com.php25.common.flux.trace.annotation.Traced;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: penghuiping
 * @date: 2019/8/5 13:35
 * @description:
 */
@Aspect
@Component
public class TracedProcess {

    @Autowired
    Tracer tracer;

    @Pointcut("@annotation(com.php25.common.flux.trace.annotation.Traced)")
    private void tracedAnnotation() {
    }//定义一个切入点

    @Around("tracedAnnotation()")
    public Object traceThing(ProceedingJoinPoint pjp) throws Throwable {
        ScopedSpan span = null;
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Traced traced = method.getDeclaredAnnotation(Traced.class);
        if (StringUtil.isBlank(traced.spanName())) {
            span = tracer.startScopedSpan(method.getName());
        } else {
            span = tracer.startScopedSpan(traced.spanName());
        }

        try {
            return pjp.proceed();
        } catch (RuntimeException | Error e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
