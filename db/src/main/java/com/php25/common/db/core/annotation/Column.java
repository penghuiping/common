package com.php25.common.db.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author penghuiping
 * @date 2020/1/9 14:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
public @interface Column {

    /**
     * The mapping column name.
     */
    String value() default "";
}
