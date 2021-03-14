package com.php25.common.ws;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import lombok.extern.log4j.Log4j2;

/**
 * @author penghuiping
 * @date 2021/3/14 12:20
 */
@Log4j2
public class InnerMsgRetryCallback implements Runnable {

    private final BaseRetryMsg msg;
    private final GlobalSession globalSession;

    public InnerMsgRetryCallback(BaseRetryMsg msg) {
        this.msg = msg;
        this.globalSession = SpringContextHolder.getBean0(GlobalSession.class);
    }

    @Override
    public void run() {
        try {
            if (msg.getCount() <= msg.getMaxRetry()) {
                ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(msg.getSessionId());
                if (null != expirationSocketSession) {
                    expirationSocketSession.put(msg);
                }
            }
        } catch (Exception e) {
            log.error("消息重发出错:{}", JsonUtil.toJson(msg), e);
        }
    }
}
