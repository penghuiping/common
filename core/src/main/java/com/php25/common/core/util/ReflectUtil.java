package com.php25.common.core.util;

import com.php25.common.core.exception.Exceptions;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射帮助类
 *
 * @author penghuiping
 * @date 2018/8/10 16:04
 */
public abstract class ReflectUtil {
    private static final ConcurrentReferenceHashMap<String, Field> fieldMap = new ConcurrentReferenceHashMap<>();
    private static final ConcurrentReferenceHashMap<String, Method> methodMap = new ConcurrentReferenceHashMap<>();


    public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
        String key = cls.getName() + name;
        if (null != parameterTypes && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                key = key + parameterTypes[i];
            }
        }
        Method method = methodMap.get(key);
        if (null == method) {
            try {
                method = cls.getDeclaredMethod(name, parameterTypes);
                methodMap.putIfAbsent(key, method);
            } catch (NoSuchMethodException e) {
                throw Exceptions.throwIllegalStateException("NoSuchMethodException", e);
            }
        }
        return method;
    }


    public static Field getField(Class cls, String name) {
        String key = cls.getName() + name;
        Field field = fieldMap.get(key);
        if (field == null) {
            try {
                field = cls.getDeclaredField(name);
                fieldMap.putIfAbsent(name, field);
            } catch (NoSuchFieldException e) {
                throw Exceptions.throwIllegalStateException("NoSuchFieldException", e);
            }
        }
        return field;
    }

}
