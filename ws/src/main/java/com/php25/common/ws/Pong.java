package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 2020/8/17 16:49
 */
public class Pong extends BaseRetryMsg {

    public static final String ACTION0 = "pong";

    public Pong() {
        this.action = ACTION0;
    }
}
