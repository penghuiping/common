package com.php25.common.mvc;

/**
 * @author penghuiping
 * @date 2015-09-24
 */
public enum ApiErrorCode {
    /**
     * 正常返回数据
     */
    ok(0),
    /**
     * 服务器错误
     */
    server_error(1001),
    /**
     * 业务逻辑错误
     */
    business_error(1002);

    public int value;

    ApiErrorCode(int value) {
        this.value = value;
    }

    public String getName() {
        return this.name();
    }
}
