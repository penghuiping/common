package com.php25.common.repeat;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.web.ApiErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author penghuiping
 * @date 2023/2/11 21:26
 */
@Aspect
@Component
public class AvoidRepeatAop {

    private final static Logger log = LoggerFactory.getLogger(AvoidRepeatAop.class);

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisLockRegistry lockRegistry;

    public AvoidRepeatAop(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.lockRegistry = new RedisLockRegistry(redisConnectionFactory, "ar_lock", 10000);
    }

    @Pointcut("@annotation(com.php25.common.repeat.AvoidRepeat)")
    private void annotation() {
    }//定义一个切入点

    @Around("annotation()")
    public Object traceThing(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Annotation[] annotations = method.getDeclaredAnnotations();
        AvoidRepeat annotation0 = (AvoidRepeat) Arrays.stream(annotations).filter(annotation -> annotation instanceof AvoidRepeat).findFirst().get();
        Class<? extends GetKeyStrategy> cls = annotation0.keyStrategy();
        GetKeyStrategy getKeyStrategy = cls.getDeclaredConstructor().newInstance();
        String key = getKeyStrategy.getKey(new Context(pjp));
        Lock lock = lockRegistry.obtain(key);
        boolean flag = true;
        try {
            flag = lock.tryLock(1, TimeUnit.SECONDS);
            if (!flag) {
                throw Exceptions.throwBusinessException(ApiErrorCode.AVOID_REPEAT);
            }
            return pjp.proceed();
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }
}
