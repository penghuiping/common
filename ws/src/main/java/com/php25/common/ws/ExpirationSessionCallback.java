package com.php25.common.ws;


import com.php25.common.core.mess.SpringContextHolder;
import lombok.extern.log4j.Log4j2;

/**
 * @author penghuiping
 * @date 2021/3/14 11:41
 */
@Log4j2
public class ExpirationSessionCallback implements Runnable {

    private final String sessionId;

    private final GlobalSession globalSession;

    public ExpirationSessionCallback(String sessionId) {
        this.sessionId = sessionId;
        this.globalSession = SpringContextHolder.getBean0(GlobalSession.class);
    }

    @Override
    public void run() {
        try {
            log.info("长时间未收到心跳包关闭ws连接");
            ConnectionClose connectionClose = new ConnectionClose();
            connectionClose.setCount(1);
            connectionClose.setMsgId(globalSession.generateUUID());
            connectionClose.setSessionId(sessionId);
            globalSession.send(connectionClose);
        } catch (Exception e) {
            log.error("未接受到心跳包，关闭session出错", e);
        }

    }
}
