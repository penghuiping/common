package com.php25.common.timer;

/**
 * @author penghuiping
 * @date 2020/5/15 17:12
 */
public class TimerException extends RuntimeException {

    public TimerException() {
    }

    public TimerException(String message) {
        super(message);
    }

    public TimerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimerException(Throwable cause) {
        super(cause);
    }

    public TimerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
