package com.php25.common.db.util;

import com.php25.common.core.exception.Exceptions;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author penghuiping
 * @date 2021/12/25 21:26
 */
public class LambdaUtil {

    public static String methodNameFromLambda(Serializable lambda) {
        try {
            Method m = lambda.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
            return sl.getImplMethodName();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String fieldNameFromLambda(Serializable lambda) {
        try {
            Method m = lambda.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
            String methodName = sl.getImplMethodName();
            if (!methodName.startsWith("get")) {
                throw Exceptions.throwIllegalStateException("实体对象需支持get方法");
            }
            methodName = methodName.substring(3);
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
