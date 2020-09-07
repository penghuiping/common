package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 2020/9/1 09:35
 */
public interface ReplyAckHandler {

    /**
     * ack回复处理类
     *
     * @param session 全局websocketSession
     * @param msg     需要回复的源消息
     */
    void handle(GlobalSession session,BaseRetryMsg msg) throws Exception;
}
