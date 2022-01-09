package com.php25.common.id.service.entity;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public class ResultCode {

    /**
     * 正常可用
     */
    public static final int NORMAL = 1;
    /**
     * 需要去加载nextId
     */
    public static final int LOADING = 2;
    /**
     * 超过maxId 不可用
     */
    public static final int OVER = 3;

    private ResultCode() {

    }
}
