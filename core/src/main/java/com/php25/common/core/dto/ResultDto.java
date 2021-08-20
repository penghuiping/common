package com.php25.common.core.dto;

import com.php25.common.core.exception.BusinessErrorStatus;

/**
 * 用于函数方法返回错误码
 *
 * @author penghuiping
 * @date 2019/1/2 14:20
 */
public class ResultDto<T> {

    /**
     * When error happened, this field used to say what error is.
     */
    private BusinessErrorStatus error;

    /**
     * When the program execution succeeded ,no error happened. Then this field stands for
     * the right execution result of the program.
     */
    private T data;

    private ResultDto() {

    }

    public static <T> ResultDto<T> error(BusinessErrorStatus error) {
        ResultDto<T> res = new ResultDto<>();
        res.error = error;
        return res;
    }

    public static <T> ResultDto<T> ok(T data) {
        ResultDto<T> res = new ResultDto<>();
        res.data = data;
        return res;
    }


    public static <T> ResultDto<T> error(String code, String description) {
        BusinessErrorStatus businessErrorStatus = new BusinessErrorStatus() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getDesc() {
                return description;
            }
        };
        return error(businessErrorStatus);
    }


    /**
     * This method is used for judging whether errors has happened
     *
     * @return true: has some errors
     */
    public boolean hasError() {
        return null != error && data == null;
    }

    public BusinessErrorStatus getError() {
        return error;
    }

    public T getData() {
        return data;
    }
}
