package com.php25.common.mq.redis;

import com.php25.common.mq.Message;

/**
 * @author penghuiping
 * @date 2021/3/11 10:50
 */
class RedisMessage {

    /**
     * 消息
     */
    private Message message;

    /**
     * 队列名
     */
    private String queue;

    public RedisMessage(Message message, String queue) {
        this.message = message;
        this.queue = queue;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
