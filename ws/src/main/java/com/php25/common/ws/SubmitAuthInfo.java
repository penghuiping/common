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

    private String token;

    public SubmitAuthInfo() {
        this.action = getAction0();
    }

    @Override
    public String getAction() {
        return getAction0();
    }

    public static String getAction0() {
        return "submit_auth_info";
    }
}
