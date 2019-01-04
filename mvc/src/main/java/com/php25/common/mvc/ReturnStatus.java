package com.php25.common.mvc;

/**
 * @author: penghuiping
 * @date: 2018/12/26 17:09
 * @description:
 */
public interface ReturnStatus {

    int getValue();

    String getDesc();

    default String toString2() {
        return String.format("%d=%s", this.getValue(), this.getDesc());
    }
}
