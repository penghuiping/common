package com.php25.common.db.core.annotation;

import com.php25.common.db.core.GenerationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author penghuiping
 * @date 2019/12/11 09:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GeneratedValue {
    GenerationType strategy() default GenerationType.AUTO;

    String generator() default "";
}
