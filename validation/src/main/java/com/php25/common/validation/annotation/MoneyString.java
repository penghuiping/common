package com.php25.common.validation.annotation;

import com.php25.common.validation.validator.MoneyValidator;
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
 * @date 2019/9/11 13:45
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = MoneyValidator.class)
@Documented
public @interface MoneyString {

    String message() default "金额格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
