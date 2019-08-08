package com.php25.common.flux.web;

/**
 * @author: penghuiping
 * @date: 2019/7/19 09:51
 * @description:
 */
public abstract class BaseResponse<T> {


    private String errorCode;

    private T returnObject;

    /**
     * 提示信息
     */
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
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
