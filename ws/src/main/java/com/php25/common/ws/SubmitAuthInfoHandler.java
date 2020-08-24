package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

/**
 * @author penghuiping
 * @date 20/8/12 16:15
 */
@Slf4j
public class SubmitAuthInfoHandler implements MsgHandler<BaseRetryMsg> {


    @Override
    public void handle(GlobalSession session, BaseRetryMsg msg) throws Exception {
        log.info("SubmitAuthInfoHandler...");
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
        replyAuthInfo.setCount(1);
        replyAuthInfo.setMsgId(msg.getMsgId());
        replyAuthInfo.setSessionId(msg.getSessionId());
        replyAuthInfo.setUid(uid);
        session.send(replyAuthInfo);
    }
}
