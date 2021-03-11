package com.php25.common.mq;

/**
 * @author penghuiping
 * @date 2021/3/10 22:28
 */
public interface MessageSubscriber {

    /**
     * 设置消息处理回调方法
     *
     * @param handler 消息处理回调方法
     */
    void setHandler(MessageHandler handler);


    /**
     * 订阅(队列+消费组)
     *
     * @param queue 队列名
     * @param group 消费组名
     */
    void subscribe(String queue, String group);

    /**
     * 订阅(队列)
     *
     * @param queue 队列名
     */
    void subscribe(String queue);


}
