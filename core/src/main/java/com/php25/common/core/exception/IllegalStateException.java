package com.php25.common.core.exception;

/**
 * @author penghuiping
 * @date 2019/7/23 16:39
 */
public class IllegalStateException extends RuntimeException {

    public IllegalStateException(String message) {
        super(message);
    }

    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
