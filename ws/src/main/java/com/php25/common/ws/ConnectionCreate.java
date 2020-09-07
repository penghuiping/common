package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 20/8/12 16:59
 */
public class ConnectionCreate extends BaseRetryMsg {

    public static final String ACTION0 = "connection_create";

    public ConnectionCreate() {
        this.action = ACTION0;
    }
}
