package com.php25.common.constant;

/**
 * Created by penghuiping on 9/24/15.
 */
public enum ApiErrorCode {
    ok(0),//
    server_error(1001),
    business_error(1002);

    public int value;

    ApiErrorCode(int value) {
        this.value = value;
    }

    public String getName() {
        return this.name();
    }
}
