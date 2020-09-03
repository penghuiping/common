package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 20/8/12 16:31
 */
@Slf4j
public class RequestAuthInfoHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        log.info("RequestAuthInfoHandler...");
        RequestAuthInfo requestAuthInfo = (RequestAuthInfo) msg;
        requestAuthInfo.setCount(requestAuthInfo.getCount() + 1);
        requestAuthInfo.setTimestamp(System.currentTimeMillis());
        requestAuthInfo.setInterval(5000);
        if (requestAuthInfo.getCount() > 3) {
            ConnectionClose connectionClose = new ConnectionClose();
            connectionClose.setCount(1);
            connectionClose.setMsgId(session.generateUUID());
            connectionClose.setSessionId(msg.sessionId);
            session.send(connectionClose);
            return;
        }
        session.send(requestAuthInfo);
    }
}
