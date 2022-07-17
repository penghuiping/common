package com.php25.common.core.exception;

/**
 * @author penghuiping
 * @date 2019/7/11 09:59
 */
public class ImpossibleException extends RuntimeException {

    public ImpossibleException() {
        super();
    }

    public ImpossibleException(String message) {
        super(message);
    }

    public ImpossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleException(Throwable cause) {
        super(cause);
    }

    protected ImpossibleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
