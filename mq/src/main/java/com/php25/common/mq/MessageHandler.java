package com.php25.common.mq;

/**
 * @author penghuiping
 * @date 2021/3/10 20:43
 */
public interface MessageHandler {

    /**
     * 处理消息
     *
     * @param message 消息
     */
    void handle(Message message);
}
