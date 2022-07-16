package com.php25.common.ws.protocal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.php25.common.ws.handler.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息基类
 *
 * @author penghuiping
 * @date 2021/8/26 16:02
 */
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "action", visible = true)
public abstract class BaseMsg {

    /**
     * websocket sessionId
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionId;

    /**
     * 消息类型或操作
     */
    private String action;

    /**
     * 消息id
     */
    @JsonProperty("msg_id")
    protected String msgId;

    /***
     * 消息发送时间
     */
    protected Long timestamp = System.currentTimeMillis();

    public BaseMsg() {
        WsMsg wsMsg = this.getClass().getDeclaredAnnotation(WsMsg.class);
        if (null != wsMsg) {
            this.setAction(wsMsg.action());
        }
    }
}
