package com.php25.common.flux.web;

/**
 * @author penghuiping
 * @date 2015-09-24
 */
public enum ApiErrorCode {
    /**
     * 正常返回数据
     */
    ok("0"),

    /**
     * 请求参数效验错误
     */
    input_params_error("1"),

    /**
     * 不支持的http方法
     */
    http_method_not_support("2"),

    /**
     * 服务器错误
     */
    unknown_error("3");

    public String value;

    ApiErrorCode(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name();
    }
}
