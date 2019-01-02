package com.php25.common.core.dto;

import java.io.Serializable;

/**
 * @author: penghuiping
 * @date: 2019/1/2 14:20
 * @description:
 */
public class ResultDto<T> implements Serializable {

    /**
     * if status is true,then the object attribute of the ResultDto is valid
     * else the object attribute should be ignored. It's maybe some wrong logic happened
     * and we can't get correct data.
     */
    private boolean status;

    /**
     * This is a general type object, and it's specific type can only be decided on
     * runtime
     */
    private T object;


    public ResultDto(boolean status, T object) {
        this.status = status;
        this.object = object;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
