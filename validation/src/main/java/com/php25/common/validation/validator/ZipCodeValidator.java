package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.ZipCode;
import com.php25.common.validation.util.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @author penghuiping
 * @date 2019/9/11 13:26
 */
public class ZipCodeValidator implements ConstraintValidator<ZipCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isZipCode(value);
    }

    @Override
    public void initialize(ZipCode constraintAnnotation) {

    }
}
