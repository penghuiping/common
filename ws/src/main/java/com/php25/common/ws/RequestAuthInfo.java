package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:10
 */
@Setter
@Getter
public class RequestAuthInfo extends BaseRetryMsg {

    public RequestAuthInfo() {
        this.action = getAction0();
    }

    @Override
    public String getAction() {
        return getAction0();
    }

    public static String getAction0() {
        return "request_auth_info";
    }
}
