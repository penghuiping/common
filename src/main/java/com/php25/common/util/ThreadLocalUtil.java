package com.php25.common.util;

/**
 * ThreadLocal帮助类
 * @author penghuiping
 * @Timer 11/17/15.
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    public static void set(Object object) {
        threadLocal.set(object);
    }

    public static Object get() {
        return threadLocal.get();
    }
}
