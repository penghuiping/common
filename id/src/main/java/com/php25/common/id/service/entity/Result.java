package com.php25.common.id.service.entity;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public class Result {
    private int code;
    private long id;

    public Result(int code, long id) {
        this.code = code;
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[id:" + id + ",code:" + code + "]";
    }
}
