package com.php25.common.core.util;

/**
 * ThreadLocal帮助类
 *
 * @author penghuiping
 * @date 11/17/15.
 */
public abstract class ThreadLocalUtil {
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<Object>();

    public static void set(Object object) {
        THREAD_LOCAL.set(object);
    }

    public static Object get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
