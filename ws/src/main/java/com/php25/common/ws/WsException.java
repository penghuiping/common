package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 2020/9/4 17:04
 */
public class WsException extends RuntimeException {

    public WsException() {
    }

    public WsException(String message) {
        super(message);
    }

    public WsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WsException(Throwable cause) {
        super(cause);
    }

    public WsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
