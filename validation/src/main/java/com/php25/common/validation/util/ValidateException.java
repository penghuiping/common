package com.php25.common.validation.util;


/**
 * 验证异常
 *
 * @author xiaoleilu
 */
public class ValidateException extends RuntimeException {
    private static final long serialVersionUID = 6057602589533840889L;

    public ValidateException() {
    }

    public ValidateException(String msg) {
        super(msg);
    }


    public ValidateException(Throwable throwable) {
        super(throwable);
    }

    public ValidateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }


}
