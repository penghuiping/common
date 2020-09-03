package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Slf4j
public class WebsocketHandler extends TextWebSocketHandler {

    private GlobalSession globalSession;

    private InnerMsgRetryQueue innerMsgRetryQueue;

    public WebsocketHandler(GlobalSession globalSession, InnerMsgRetryQueue innerMsgRetryQueue) {
        this.globalSession = globalSession;
        this.innerMsgRetryQueue = innerMsgRetryQueue;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        BaseRetryMsg baseRetryMsg = JsonUtil.fromJson(payload, BaseRetryMsg.class);
        baseRetryMsg.setSessionId(session.getId());
        baseRetryMsg.setCount(0);
        innerMsgRetryQueue.put(baseRetryMsg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ConnectionClose connectionClose = new ConnectionClose();
        connectionClose.setAction(ConnectionClose.getAction0());
        connectionClose.setCount(1);
        connectionClose.setMsgId(globalSession.generateUUID());
        connectionClose.setSessionId(session.getId());
        globalSession.send(connectionClose);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        globalSession.create(session);
        ConnectionCreate connectionCreate = new ConnectionCreate();
        connectionCreate.setAction(ConnectionCreate.getAction0());
        connectionCreate.setCount(1);
        connectionCreate.setMsgId(globalSession.generateUUID());
        connectionCreate.setSessionId(session.getId());
        globalSession.send(connectionCreate);
    }


}
