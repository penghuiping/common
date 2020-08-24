package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author penghuiping
 * @date 20/8/12 16:52
 */
@Slf4j
public class ConnectionCloseHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        log.info("ConnectionCloseHandler...");
        ConnectionClose requestClose = (ConnectionClose) msg;
        WebSocketSession webSocketSession = session.get(msg.getSessionId());
        if (null != webSocketSession && webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        session.clean(msg.getSessionId());
    }
}
