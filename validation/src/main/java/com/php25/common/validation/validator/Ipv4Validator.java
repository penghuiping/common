package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.Ipv4;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author penghuiping
 * @date 2019/9/11 13:36
 */
public class Ipv4Validator implements ConstraintValidator<Ipv4, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isIpv4(value);
    }

    @Override
    public void initialize(Ipv4 constraintAnnotation) {

    }
}
