package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.Email;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @author penghuiping
 * @date 2019/9/11 13:22
 */
public class EmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isEmail(value);
    }

    @Override
    public void initialize(Email constraintAnnotation) {

    }
}
