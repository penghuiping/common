package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2020/8/13 17:42
 */
@Setter
@Getter
@WsMsg(action = "reply_auth_info")
public class ReplyAuthInfo extends BaseRetryMsg {
    private String uid;
}
