package com.php25.common.validation.annotation;

import com.php25.common.validation.validator.MobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author penghuiping
 * @date 2019/9/11 13:25
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = MobileValidator.class)
@Documented
public @interface Mobile {

    String message() default "手机号不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
