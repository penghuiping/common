package com.php25.common.core.exception;

/**
 * @author: penghuiping
 * @date: 2018/12/26 17:09
 * @description:
 */
public interface BusinessErrorStatus {

    String getCode();

    String getDesc();

    default String toString2() {
        return String.format("%s=%s", this.getCode(), this.getDesc());
    }
}
