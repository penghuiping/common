package com.php25.common.ws;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.retry.DefaultRetryQueue;
import com.php25.common.ws.retry.RejectAction;
import com.php25.common.ws.retry.RetryQueue;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 消息重发器,此类用于实现延时消息重发
 * 对于服务端push消息，客户端需要回ack,如果服务端没有收到客户端回的ack的消息,则服务端需要重发
 * 服务端默认5秒没收到ack消息,进行原消息重发
 * 最大重发次数为5次
 *
 * @author penghuiping
 * @date 2021/8/26 15:56
 */
@Log4j2
public class RetryMsgManager implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    private final RetryQueue<BaseMsg> retryQueue;

    private SessionContext globalSession;

    public RetryMsgManager() {
        this.retryQueue = new DefaultRetryQueue<>(5, 5000L,
                retryMsg -> {
                    //重试逻辑
                    ExpirationSocketSession expirationSocketSession = this.getGlobalSession().getExpirationSocketSession(retryMsg.getSessionId());
                    if (null != expirationSocketSession) {
                        expirationSocketSession.put(retryMsg);
                    }
                },
                retryMsg -> {
                    //重试超过5次拒绝逻辑
                    log.warn("此消息重试超过五次依旧失败:{}", JsonUtil.toJson(retryMsg));
                });
    }

    public SessionContext getGlobalSession() {
        if (this.globalSession == null) {
            this.globalSession = SpringContextHolder.getBean0(SessionContext.class);
        }
        return globalSession;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent contextClosedEvent) {
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public void put(BaseMsg baseMsg) {
        this.put(baseMsg, null);

    }

    public void put(BaseMsg baseMsg, RejectAction<BaseMsg> rejectAction) {
        //需要重试
        retryQueue.offer(baseMsg.getMsgId(), baseMsg, rejectAction);
    }

    public void remove(String msgId) {
        retryQueue.remove(msgId);
    }

    public BaseMsg get(String msgId) {
        return retryQueue.get(msgId);
    }

    public void stats() {
        log.info("InnerMsgRetryQueue msgs:{}", retryQueue.size());
    }
}
