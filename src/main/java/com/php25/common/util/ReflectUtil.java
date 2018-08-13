package com.php25.common.util;

import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/10 16:04
 * @Description:
 */
public class ReflectUtil {
    private static ConcurrentReferenceHashMap<String, Field> fieldMap = new ConcurrentReferenceHashMap<>();
    private static ConcurrentReferenceHashMap<String, Method> methodMap = new ConcurrentReferenceHashMap<>();


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
                throw new RuntimeException("NoSuchMethodException", e);
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
                throw new RuntimeException("NoSuchFieldException", e);
            }
        }
        return field;
    }

}
