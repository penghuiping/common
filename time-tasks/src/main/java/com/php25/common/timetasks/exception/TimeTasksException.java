package com.php25.common.timetasks.exception;

/**
 * @author penghuiping
 * @date 2020/5/15 17:12
 */
public class TimeTasksException extends RuntimeException {

    public TimeTasksException() {
    }

    public TimeTasksException(String message) {
        super(message);
    }

    public TimeTasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeTasksException(Throwable cause) {
        super(cause);
    }

    public TimeTasksException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
