package com.php25.common.ws;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.ws.handler.MsgHandler;
import com.php25.common.ws.protocal.BaseMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息分发器
 *
 * @author penghuiping
 * @date 2020/08/10
 */
@Slf4j
public class MsgDispatcher {

    private final ConcurrentHashMap<String, MsgHandler<BaseMsg>> handlers = new ConcurrentHashMap<>();

    private SessionContext session;

    public SessionContext getSession() {
        if (session == null) {
            this.session = SpringContextHolder.getBean0(SessionContext.class);
        }
        return session;
    }

    public void dispatch(BaseMsg baseMsg) {
        try {
            String action = baseMsg.getAction();
            MsgHandler<BaseMsg> handler = handlers.get(action);
            if (null != handler) {
                handler.handle(getSession(), baseMsg);
            }
        } catch (Exception e) {
            log.error("发送websocket消息出错", e);
        }
    }

    public void registerHandler(String name, MsgHandler<BaseMsg> msgHandler) {
        handlers.put(name, msgHandler);
    }
}
