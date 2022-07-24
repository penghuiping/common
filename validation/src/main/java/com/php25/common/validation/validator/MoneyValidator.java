package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.MoneyString;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author penghuiping
 * @date 2019/9/11 13:45
 */
public class MoneyValidator implements ConstraintValidator<MoneyString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isMoney(value);
    }

    @Override
    public void initialize(MoneyString constraintAnnotation) {

    }
}
