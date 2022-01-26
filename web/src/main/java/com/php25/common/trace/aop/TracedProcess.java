package com.php25.common.trace.aop;

import brave.ScopedSpan;
import brave.Tracer;
import com.php25.common.core.util.StringUtil;
import com.php25.common.trace.annotation.Traced;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoField;

/**
 * 调用链埋点aop方式，对注解 com.php25.common.flux.trace.annotation.Traced 进行相关注入操作
 *
 * @author penghuiping
 * @date 2019/8/5 13:35
 */
@Aspect
@ConditionalOnBean(value = {Tracer.class})
@Component
public class TracedProcess {

    private final static Logger log = LoggerFactory.getLogger(TracedProcess.class);

    @Autowired
    Tracer tracer;

    @Pointcut("@annotation(com.php25.common.trace.annotation.Traced)")
    private void tracedAnnotation() {
    }//定义一个切入点

    @Around("tracedAnnotation()")
    public Object traceThing(ProceedingJoinPoint pjp) throws Throwable {
        ScopedSpan span;
        long start = Instant.now().getLong(ChronoField.MILLI_OF_SECOND);
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        long end = Instant.now().getLong(ChronoField.MILLI_OF_SECOND);
        log.info("Traced:{},方法名:{},耗时:{}ms", tracer.currentSpan(), method.getName(), end - start);
        Traced traced = method.getDeclaredAnnotation(Traced.class);
        if (null == traced || StringUtil.isBlank(traced.spanName())) {
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
