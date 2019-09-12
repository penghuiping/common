package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.Ipv6;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author penghuiping
 * @date 2019/9/11 13:40
 */
public class Ipv6Validator implements ConstraintValidator<Ipv6, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isIpv6(value);
    }

    @Override
    public void initialize(Ipv6 constraintAnnotation) {

    }
}
