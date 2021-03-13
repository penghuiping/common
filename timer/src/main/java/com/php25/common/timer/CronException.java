package com.php25.common.timer;

/**
 * @author penghuiping
 * @date 2020/5/18 13:26
 */
public class CronException extends RuntimeException {

    public CronException() {
        super();
    }

    public CronException(String message) {
        super(message);
    }

    public CronException(String message, Throwable cause) {
        super(message, cause);
    }

    public CronException(Throwable cause) {
        super(cause);
    }

    protected CronException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
