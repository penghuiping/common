package com.php25.common.validation.validator;

import com.php25.common.validation.annotation.IdCard;
import com.php25.common.validation.util.IdcardUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @author penghuiping
 * @date 2019/9/9 15:54
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return IdcardUtil.isValidCard(value);
    }

    @Override
    public void initialize(IdCard constraintAnnotation) {

    }
}
