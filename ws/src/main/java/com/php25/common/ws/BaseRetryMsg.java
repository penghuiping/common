package com.php25.common.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 所有需要重发的消息需要继承此抽象类
 *
 * @author penghuiping
 * @date 20/8/11 10:58
 */

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "action", visible = true)
public class BaseRetryMsg implements Delayed {

    /**
     * 重发间隔,单位毫秒,默认0秒
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer interval = 0;
    /**
     * 当前重发次数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer count = 0;
    /**
     * 最大重发次数，默认5次
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxRetry = 5;

    /**
     * websocket sessionId
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String sessionId;

    /**
     * 消息id
     */
    @JsonProperty("msg_id")
    protected String msgId;

    /**
     * 消息类型或操作
     */
    protected String action;


    /***
     * 消息发送时间
     */
    protected Long timestamp = System.currentTimeMillis();

    public BaseRetryMsg() {
        WsMsg wsMsg = this.getClass().getDeclaredAnnotation(WsMsg.class);
        if (null != wsMsg) {
            this.setAction(wsMsg.action());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        BaseRetryMsg that = (BaseRetryMsg) o;
        return Objects.equal(msgId, that.msgId) &&
                Objects.equal(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(msgId, action);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return (interval - (System.currentTimeMillis() - timestamp)) * 1000000;
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
    }
}
