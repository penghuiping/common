package com.php25.common.db.mapper.exception;

/**
 * @author penghuiping
 * @date 2022/1/15 20:55
 */
public class DbMapperException extends RuntimeException {
    public DbMapperException() {
        super();
    }

    public DbMapperException(String message) {
        super(message);
    }

    public DbMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbMapperException(Throwable cause) {
        super(cause);
    }

    protected DbMapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
