package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2020/8/13 17:42
 */
@Setter
@Getter
public class ReplyAuthInfo extends BaseRetryMsg {

    private String uid;

    public ReplyAuthInfo() {
        this.action = getAction0();
    }

    @Override
    public String getAction() {
        return getAction0();
    }

    public static String getAction0() {
        return "reply_auth_info";
    }
}
