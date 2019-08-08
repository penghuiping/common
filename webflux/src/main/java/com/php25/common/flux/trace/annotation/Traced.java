package com.php25.common.flux.trace.annotation;

/**
 * @author: penghuiping
 * @date: 2019/8/5 13:34
 * @description:
 */
public @interface Traced {
    String spanName() default "";
}
