package com.php25.common.repeat;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2023/2/11 22:26
 */
public class ShaHashKeyStrategy implements GetKeyStrategy {

    @Override
    public String getKey(Context context) {
        ProceedingJoinPoint pjp = context.getPjp();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Object[] args = pjp.getArgs();
        List<Object> args0 = Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse)).collect(Collectors.toList());
        Object[] obj = new Object[]{className, methodName, args0};
        return DigestUtil.shaStr(JsonUtil.toJson(obj));
    }
}
