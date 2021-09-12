package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.protocal.ConnectionClose;
import com.php25.common.ws.protocal.ConnectionCreate;
import com.php25.common.ws.protocal.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * websocket的消息通讯的入口
 *
 * @author penghuiping
 * @date 2021-08-28
 */
@Slf4j
public class WebsocketHandler extends TextWebSocketHandler {


    private final SessionContext sessionContext;

    public WebsocketHandler(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        BaseMsg baseMsg = JsonUtil.fromJson(payload, BaseMsg.class);
        if (!(baseMsg instanceof Ping)) {
            log.info("ws request msg:{}", JsonUtil.toJson(baseMsg));
        }
        ExpirationSocketSession expirationSocketSession = this.sessionContext.getExpirationSocketSession(session);
        if (null == expirationSocketSession) {
            log.info("expirationSocketSession is null:{}", JsonUtil.toJson(baseMsg));
            return;
        }
        baseMsg.setSessionId(expirationSocketSession.getSessionId());
        expirationSocketSession.put(baseMsg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ConnectionClose connectionClose = new ConnectionClose();
        connectionClose.setMsgId(this.sessionContext.generateUUID());
        ExpirationSocketSession expirationSocketSession = this.sessionContext.getExpirationSocketSession(session);
        connectionClose.setSessionId(expirationSocketSession.getSessionId());
        expirationSocketSession.put(connectionClose);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //websocket连接建立
        this.sessionContext.create(session);
        ConnectionCreate connectionCreate = new ConnectionCreate();
        connectionCreate.setMsgId(this.sessionContext.generateUUID());
        ExpirationSocketSession expirationSocketSession = this.sessionContext.getExpirationSocketSession(session);
        connectionCreate.setSessionId(expirationSocketSession.getSessionId());
        expirationSocketSession.put(connectionCreate);
    }


}
