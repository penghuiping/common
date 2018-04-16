package com.php25.common.dto;

/**
 * 服务器返回对象
 * 所有服务器处理返回的统一对象
 */
public class JSONResponse {
    //0没有错误一些正常  1001服务器错误 1002业务逻辑错误
    private int errorCode;
    //成功时返回的对象
    private Object returnObject = null;
    //提示信息
    private String message;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
