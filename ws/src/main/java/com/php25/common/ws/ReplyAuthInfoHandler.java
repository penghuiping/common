package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 2020/8/13 17:42
 */
@Slf4j
public class ReplyAuthInfoHandler implements MsgHandler<BaseRetryMsg> {

    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        ReplyAuthInfo replyAuthInfo = (ReplyAuthInfo) msg;
        replyAuthInfo.setCount(replyAuthInfo.getCount() + 1);
        replyAuthInfo.setInterval(5000);
        session.send(replyAuthInfo);
    }
}
