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

    public static final String ACTION0 = "request_auth_info";

    public RequestAuthInfo() {
        this.action = ACTION0;
    }


}
