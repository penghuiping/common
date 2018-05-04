package com.php25.common.aop;

import com.php25.common.exception.JsonException;
import com.php25.common.exception.ModelAndViewException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by penghuiping on 2016/12/23.
 */
@Aspect
@Component
public class ControllerAOP {
    private static Logger logger = LoggerFactory.getLogger(ControllerAOP.class);

    //@Pointcut("execution(* com.joinsoft..*.*Controller.*(..))")
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    private void anyMethod() {
    }//定义一个切入点


    @Around("anyMethod()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object object = pjp.proceed();//执行该方法
            return object;
        } catch (Exception e) {
            logger.error("出错啦!!", e);
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            Class[] exceptions = method.getExceptionTypes();
            if (exceptions != null && exceptions.length >= 1) {
                if (exceptions[0].equals(JsonException.class)) {
                    throw new JsonException(e);
                } else if (exceptions[0].equals(ModelAndViewException.class)) {
                    throw new ModelAndViewException(e);
                }
            }
        }
        return null;
    }
}
