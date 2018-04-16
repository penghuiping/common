package com.php25.common.exception;

/**
 * Created by penghuiping on 2016/12/23.
 */
public class ModelAndViewException extends Exception {

    public ModelAndViewException() {
    }

    public ModelAndViewException(String message) {
        super(message);
    }

    public ModelAndViewException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelAndViewException(Throwable cause) {
        super(cause);
    }

    public ModelAndViewException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
