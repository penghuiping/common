package com.php25.common.mvc;

/**
 *
 * @author penghuiping
 * @date 2016/12/23.
 *
 * json错误，一般用于controller返回json数据
 *
 */
public class JsonException extends Exception {

    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
