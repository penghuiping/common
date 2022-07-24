package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.Mobile;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @author penghuiping
 * @date 2019/9/11 13:26
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isMobile(value);
    }

    @Override
    public void initialize(Mobile constraintAnnotation) {

    }
}
