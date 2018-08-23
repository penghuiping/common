package com.php25.common.mvc;

/**
 * @author penghuiping
 * @date 2018/5/17 11:04
 *
 * 统一对ModelAndView异常的管理
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
