package com.php25.common.id.exception;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public class IdGeneratorException extends RuntimeException {

    public IdGeneratorException() {
        super();
    }

    public IdGeneratorException(String message) {
        super(message);
    }

    public IdGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdGeneratorException(Throwable cause) {
        super(cause);
    }
}
