package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 2020/8/17 16:47
 */
@Slf4j
public class PingHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
//        log.info("心跳ping:{}", JsonUtil.toJson(msg));
        Ping ping = (Ping) msg;
        session.updateExpireTime(ping.getSessionId());
        Pong pong = new Pong();
        pong.setMsgId(ping.getMsgId());
        pong.setAction(Pong.getAction0());
        pong.setSessionId(ping.getSessionId());
        pong.setMaxRetry(1);
        session.send(pong,false);

    }
}
