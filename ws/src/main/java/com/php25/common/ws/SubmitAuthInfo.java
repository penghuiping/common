package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:15
 */
@Setter
@Getter
public class SubmitAuthInfo extends BaseRetryMsg {

    public static final String ACTION0 = "submit_auth_info";

    private String token;

    public SubmitAuthInfo() {
        this.action = ACTION0;
    }
}
