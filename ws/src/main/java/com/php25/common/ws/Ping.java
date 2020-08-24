package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 2020/8/17 16:47
 */
public class Ping extends BaseRetryMsg {

    @Override
    public String getAction() {
        return getAction0();
    }

    public static String getAction0() {
        return "ping";
    }
}
