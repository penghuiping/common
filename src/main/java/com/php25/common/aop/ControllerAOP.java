package com.php25.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.exception.JsonException;
import com.php25.common.exception.ModelAndViewException;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 2016/12/23.
 */
@Aspect
@Component
public class ControllerAOP {
    @Pointcut("execution(* com.joinsoft..*.*Controller.*(..))")
    private void anyMethod() {
    }//定义一个切入点

    @Autowired
    private ObjectMapper objectMapper;

    @Around("anyMethod()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object object = pjp.proceed();//执行该方法
            return object;
        } catch (Exception e) {
            Logger.getLogger(ControllerAOP.class).error(e);
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            if(method.getDeclaringClass().isAnnotationPresent(RestController.class)) {
                if(e instanceof ConstraintViolationException) {
                    List<String> messages = ((ConstraintViolationException) e).getConstraintViolations().stream().map(a->a.getPropertyPath()+a.getMessage()).collect(Collectors.toList());
                    String message = objectMapper.writeValueAsString(messages);
                    throw new ConstraintViolationException(message,null);
                }
                throw e;
            }else {
                Class[] exceptions = method.getExceptionTypes();
                if (1 == exceptions.length) {
                    if (exceptions[0].equals(JsonException.class)) {
                        throw new JsonException(e);
                    } else if (exceptions[0].equals(ModelAndViewException.class)) {
                        throw new ModelAndViewException(e);
                    }
                }
            }
        }
        return null;
    }
}
