package com.php25.common.flux;

/**
 * @author: penghuiping
 * @date: 2019/7/19 09:51
 * @description:
 */
public abstract class BaseResponse<T> {

    /**
     * 0:没有错误一些正常  1001:服务器错误 1002: 业务逻辑错误
     */
    private int errorCode;

    private T returnObject;

    /**
     * 提示信息
     */
    private String message;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(T returnObject) {
        this.returnObject = returnObject;
    }
}
