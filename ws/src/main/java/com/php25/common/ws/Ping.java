package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 2020/8/17 16:47
 */
public class Ping extends BaseRetryMsg {

    public static final String ACTION0 = "ping";

    public Ping() {
        this.action = ACTION0;
    }
}
