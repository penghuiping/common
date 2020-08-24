package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 2020/8/13 17:45
 */
@Slf4j
public class AckHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        Ack ack = (Ack) msg;
        log.info("ack...;msgid:{},reply_action:{}", ack.getMsgId(), ack.getReplyAction());
        BaseRetryMsg baseRetryMsg = new BaseRetryMsg();
        baseRetryMsg.setMsgId(msg.getMsgId());
        baseRetryMsg.setAction(ack.getReplyAction());
        session.revokeRetry(baseRetryMsg);
    }
}
