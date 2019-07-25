package com.php25.common.core.exception;

/**
 * @author: penghuiping
 * @date: 2019/7/11 09:44
 * @description:
 */
public class BusinessException extends RuntimeException {
    private String code;


    public BusinessException(BusinessErrorStatus businessErrorStatus) {
        this(businessErrorStatus.getCode(), businessErrorStatus.getDesc());
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessErrorStatus getBusinessErrorStatus() {
        return new BusinessErrorStatus() {
            @Override
            public String getCode() {
                return BusinessException.this.getCode();
            }

            @Override
            public String getDesc() {
                return BusinessException.this.getMessage();
            }
        };
    }

    public String getCode() {
        return code;
    }
}
