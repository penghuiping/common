package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Slf4j
public class WebsocketHandler extends TextWebSocketHandler {

    private final GlobalSession globalSession;

    private final InnerMsgRetryQueue innerMsgRetryQueue;

    public WebsocketHandler(GlobalSession globalSession, InnerMsgRetryQueue innerMsgRetryQueue) {
        this.globalSession = globalSession;
        this.innerMsgRetryQueue = innerMsgRetryQueue;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        BaseRetryMsg baseRetryMsg = JsonUtil.fromJson(payload, BaseRetryMsg.class);
        if (!(baseRetryMsg instanceof Ping)) {
            log.info("ws request msg:{}", JsonUtil.toJson(baseRetryMsg));
        }
        ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(session);
        if (null == expirationSocketSession) {
            log.info("expirationSocketSession is null:{}", JsonUtil.toJson(baseRetryMsg));
            return;
        }
        baseRetryMsg.setSessionId(expirationSocketSession.getSessionId());
        innerMsgRetryQueue.put(baseRetryMsg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ConnectionClose connectionClose = new ConnectionClose();
        connectionClose.setMsgId(globalSession.generateUUID());
        ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(session);
        connectionClose.setSessionId(expirationSocketSession.getSessionId());
        globalSession.send(connectionClose);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //websocket连接建立
        globalSession.create(session);
        ConnectionCreate connectionCreate = new ConnectionCreate();
        connectionCreate.setMsgId(globalSession.generateUUID());
        ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(session);
        connectionCreate.setSessionId(expirationSocketSession.getSessionId());
        globalSession.send(connectionCreate);
    }


}
