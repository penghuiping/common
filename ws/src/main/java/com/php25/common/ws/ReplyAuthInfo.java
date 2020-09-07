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

    public static final String ACTION0 = "reply_auth_info";

    private String uid;

    public ReplyAuthInfo() {
        this.action = ACTION0;
    }


}
