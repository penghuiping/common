package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 20/8/12 16:51
 */
public class ConnectionClose extends BaseRetryMsg {


    public ConnectionClose() {
        this.action = getAction0();
    }

    @Override
    public String getAction() {
        return getAction0();
    }

    public static String getAction0() {
        return "connection_close";
    }
}
