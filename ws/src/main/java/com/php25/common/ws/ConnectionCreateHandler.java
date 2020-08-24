package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 20/8/12 17:00
 */
@Slf4j
public class ConnectionCreateHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        log.info("ConnectionCreateHandler...");
        ConnectionCreate connectionCreate = (ConnectionCreate) msg;
        //发送身份认证请求
        RequestAuthInfo requestAuthInfo = new RequestAuthInfo();
        requestAuthInfo.setCount(0);
        requestAuthInfo.setMsgId(connectionCreate.getMsgId());
        requestAuthInfo.setSessionId(msg.getSessionId());
        requestAuthInfo.setInterval(5000);
        session.send(requestAuthInfo);
    }
}
