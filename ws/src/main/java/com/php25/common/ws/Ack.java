package com.php25.common.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2020/8/13 17:44
 */
@Setter
@Getter
@WsMsg(action = "ack")
public class Ack extends BaseRetryMsg {
    @JsonProperty("reply_action")
    private String replyAction;
}
