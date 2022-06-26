package com.php25.common.validation.annotation;

import com.php25.common.validation.validator.IdCardValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author penghuiping
 * @date 2019/9/9 15:51
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = IdCardValidator.class)
@Documented
public @interface IdCard {

    String message() default "中国身份证号不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


