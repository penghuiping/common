package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 20/8/12 16:51
 */
public class ConnectionClose extends BaseRetryMsg {

    public static final String ACTION0 = "connection_close";

    public ConnectionClose() {
        this.action = ACTION0;
    }
}
