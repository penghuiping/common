package com.php25.common.mq;


import java.util.List;

/**
 * 消息队列相关操作
 *
 * @author penghuiping
 * @date 2021/3/10 19:52
 */
public interface MessageQueueManager {

    /**
     * 订阅(队列名:消费组名)
     *
     * @param queue      队列名
     * @param group      消费组名
     * @param subscriber 消费者
     * @return true:订阅关系绑定成功
     */
    Boolean subscribe(String queue, String group, MessageSubscriber subscriber);

    /**
     * 往队列里发送消息
     *
     * @param queue   队列名
     * @param message 消息
     * @return true: 表示已经发送到队列
     */
    Boolean send(String queue, Message message);


    /**
     * 对某个messageId进行ack:用于表示消费成功
     *
     * @param messageId 消息id
     * @return true: ack成功
     */
    Boolean ack(String messageId);

    /**
     * 给某个队列绑定死信队列
     *
     * @param queue 队列名
     * @param dlq   死信队列名
     * @return true: 绑定成功
     */
    Boolean bindDeadLetterQueue(String queue, String dlq);

    /**
     * 获取系统中所有队列
     *
     * @return 系统中目前所有的队列名
     */
    List<String> queues();
}
