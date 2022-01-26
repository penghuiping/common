package com.php25.common.web;

import com.php25.common.core.exception.BusinessErrorStatus;

/**
 * @author penghuiping
 * @date 2015-09-24
 */
public enum ApiErrorCode implements BusinessErrorStatus {
    /**
     * 正常返回数据
     */
    ok("00000", "success"),

    /**
     * 请求参数效验错误
     */
    input_params_error("A0001", "参数效验错误"),

    /**
     * 不支持的http方法
     */
    http_method_not_support("A0002", "不支持的http方法"),

    /**
     * 服务器错误
     */
    unknown_error("B0001", "服务器错误,请联系管理员");

    public String code;

    public String description;

    ApiErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.description;
    }
}
