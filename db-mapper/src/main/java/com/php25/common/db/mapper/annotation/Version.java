package com.php25.common.db.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author penghuiping
 * @date 2022/1/15 21:30
 */
@Documented
@Retention(RUNTIME)
@Target(value = {FIELD, METHOD, ANNOTATION_TYPE})
public @interface Version {

}
