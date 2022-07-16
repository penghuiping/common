package com.php25.common.ws.handler;

import com.php25.common.ws.core.ExpirationSocketSession;
import com.php25.common.ws.core.SessionContext;
import com.php25.common.ws.core.SidUid;
import com.php25.common.ws.protocal.Ack;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.protocal.ConnectionClose;
import com.php25.common.ws.protocal.ConnectionCreate;
import com.php25.common.ws.protocal.Ping;
import com.php25.common.ws.protocal.Pong;
import com.php25.common.ws.protocal.ReplyAuthInfo;
import com.php25.common.ws.protocal.RequestAuthInfo;
import com.php25.common.ws.protocal.SubmitAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author penghuiping
 * @date 2020/9/4 14:32
 */
@Slf4j
@WsController
public class DefaultMsgHandlers {

    @WsAction("ack")
    public void ackHandler(SessionContext session, BaseMsg msg) throws Exception {
        Ack ack = (Ack) msg;
        session.getRetryMsgManager().remove(ack.getMsgId());
    }

    @WsAction("connection_close")
    public void connectionCloseHandler(SessionContext session, BaseMsg msg) throws Exception {
        ConnectionClose requestClose = (ConnectionClose) msg;
        WebSocketSession webSocketSession = session.get(msg.getSessionId());
        if (null != webSocketSession && webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        session.close(msg.getSessionId());
        session.clean(msg.getSessionId());
    }

    @WsAction("connection_create")
    public void connectionCreateHandler(SessionContext session, BaseMsg msg) throws Exception {
        ConnectionCreate connectionCreate = (ConnectionCreate) msg;
        //发送身份认证请求
        RequestAuthInfo requestAuthInfo = new RequestAuthInfo();
        requestAuthInfo.setMsgId(connectionCreate.getMsgId());
        requestAuthInfo.setSessionId(msg.getSessionId());
        requestAuthInfo.setTimestamp(System.currentTimeMillis());
        session.send(requestAuthInfo);
    }

    @WsAction("ping")
    public void pingHandler(SessionContext session, BaseMsg msg) throws Exception {
        Ping ping = (Ping) msg;
        session.updateExpireTime(ping.getSessionId());
        Pong pong = new Pong();
        pong.setMsgId(ping.getMsgId());
        pong.setSessionId(ping.getSessionId());
        session.send(pong);
    }

    @WsAction("reply_auth_info")
    public void replyAuthInfoHandler(SessionContext session, BaseMsg msg) throws Exception {
        ReplyAuthInfo replyAuthInfo = (ReplyAuthInfo) msg;
        replyAuthInfo.setTimestamp(System.currentTimeMillis());
        session.send(replyAuthInfo);
    }

    @WsAction("request_auth_info")
    public void requestAuthInfoHandler(SessionContext session, BaseMsg msg) throws Exception {
        RequestAuthInfo requestAuthInfo = (RequestAuthInfo) msg;
        requestAuthInfo.setTimestamp(System.currentTimeMillis());
        session.send(requestAuthInfo);
        session.getRetryMsgManager().put(requestAuthInfo, value -> {
            ConnectionClose connectionClose = new ConnectionClose();
            connectionClose.setMsgId(session.generateUUID());
            connectionClose.setSessionId(value.getSessionId());
            ExpirationSocketSession expirationSocketSession = session.getExpirationSocketSession(value.getSessionId());
            expirationSocketSession.put(connectionClose);
        });
    }

    @WsAction("submit_auth_info")
    public void submitAuthInfoHandler(SessionContext session, BaseMsg msg) throws Exception {
        SubmitAuthInfo submitAuthInfo = (SubmitAuthInfo) msg;
        String uid = session.authenticate(submitAuthInfo.getToken());
        SidUid sidUid = new SidUid();
        sidUid.setServerId(session.getServerId());
        sidUid.setSessionId(msg.getSessionId());
        sidUid.setUserId(uid);
        session.init(sidUid);
        session.getRetryMsgManager().remove(msg.getMsgId());

        ReplyAuthInfo replyAuthInfo = new ReplyAuthInfo();
        replyAuthInfo.setMsgId(msg.getMsgId());
        replyAuthInfo.setSessionId(msg.getSessionId());
        replyAuthInfo.setUid(uid);
        session.send(replyAuthInfo);
    }


}
