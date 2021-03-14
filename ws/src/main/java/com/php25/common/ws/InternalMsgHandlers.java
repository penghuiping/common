package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author penghuiping
 * @date 2020/9/4 14:32
 */
@Slf4j
@WsController
public class InternalMsgHandlers {

    @WsAction("ack")
    public void ackHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        Ack ack = (Ack) msg;
        BaseRetryMsg srcMsg = session.getMsg(ack.getMsgId(), ack.getReplyAction());
        if (null == srcMsg) {
            return;
        }
        session.dispatchAck(ack.getReplyAction(), srcMsg);
        BaseRetryMsg baseRetryMsg = new BaseRetryMsg();
        baseRetryMsg.setMsgId(msg.getMsgId());
        baseRetryMsg.setAction(ack.getReplyAction());
        session.revokeRetry(baseRetryMsg);
    }

    @WsAction("connection_close")
    public void connectionCloseHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        ConnectionClose requestClose = (ConnectionClose) msg;
        WebSocketSession webSocketSession = session.get(msg.getSessionId());
        if (null != webSocketSession && webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        session.close(msg.getSessionId());
        session.clean(msg.getSessionId());
    }

    @WsAction("connection_create")
    public void connectionCreateHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        ConnectionCreate connectionCreate = (ConnectionCreate) msg;
        //发送身份认证请求
        RequestAuthInfo requestAuthInfo = new RequestAuthInfo();
        requestAuthInfo.setMsgId(connectionCreate.getMsgId());
        requestAuthInfo.setSessionId(msg.getSessionId());
        requestAuthInfo.setInterval(5000);
        requestAuthInfo.setTimestamp(System.currentTimeMillis());
        session.send(requestAuthInfo);
    }

    @WsAction("ping")
    public void pingHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        Ping ping = (Ping) msg;
        session.updateExpireTime(ping.getSessionId());
        Pong pong = new Pong();
        pong.setMsgId(ping.getMsgId());
        pong.setSessionId(ping.getSessionId());
        pong.setMaxRetry(1);
        session.send(pong, false);
    }

    @WsAction("reply_auth_info")
    public void replyAuthInfoHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        ReplyAuthInfo replyAuthInfo = (ReplyAuthInfo) msg;
        replyAuthInfo.setCount(replyAuthInfo.getCount() + 1);
        replyAuthInfo.setTimestamp(System.currentTimeMillis());
        replyAuthInfo.setInterval(5000);
        session.send(replyAuthInfo);
    }

    @WsAction("request_auth_info")
    public void requestAuthInfoHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        RequestAuthInfo requestAuthInfo = (RequestAuthInfo) msg;
        requestAuthInfo.setCount(requestAuthInfo.getCount() + 1);
        requestAuthInfo.setTimestamp(System.currentTimeMillis());
        requestAuthInfo.setInterval(5000);
        //第5次 断开连接
        if (requestAuthInfo.getCount() > 4) {
            ConnectionClose connectionClose = new ConnectionClose();
            connectionClose.setMsgId(session.generateUUID());
            connectionClose.setSessionId(msg.sessionId);
            session.send(connectionClose);
            return;
        }
        session.send(requestAuthInfo);
    }

    @WsAction("submit_auth_info")
    public void submitAuthInfoHandler(GlobalSession session, BaseRetryMsg msg) throws Exception {
        SubmitAuthInfo submitAuthInfo = (SubmitAuthInfo) msg;
        String uid = session.authenticate(submitAuthInfo.getToken());
        SidUid sidUid = new SidUid();
        sidUid.setServerId(session.getServerId());
        sidUid.setSessionId(msg.getSessionId());
        sidUid.setUserId(uid);
        session.init(sidUid);

        RequestAuthInfo requestAuthInfo = new RequestAuthInfo();
        requestAuthInfo.setMsgId(msg.getMsgId());
        session.revokeRetry(requestAuthInfo);

        ReplyAuthInfo replyAuthInfo = new ReplyAuthInfo();
        replyAuthInfo.setInterval(5000);
        replyAuthInfo.setMsgId(msg.getMsgId());
        replyAuthInfo.setSessionId(msg.getSessionId());
        replyAuthInfo.setUid(uid);
        session.send(replyAuthInfo);
    }


}
