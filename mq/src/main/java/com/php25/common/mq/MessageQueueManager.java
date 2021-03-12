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
     * 订阅(队列名)
     *
     * @param queue      队列名
     * @param subscriber 消费者
     * @return true:订阅关系绑定成功
     */
    Boolean subscribe(String queue, MessageSubscriber subscriber);


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
     * 指定队列名,发送消息
     *
     * @param queue   队列名
     * @param message 消息
     * @return true: 表示已经发送到队列
     */
    Boolean send(String queue, Message message);

    /**
     * 指定队列名与组名,发送消息
     *
     * @param queue   队列名
     * @param group   组名名
     * @param message 消息
     * @return true: 表示已经发送到队列
     */
    Boolean send(String queue, String group, Message message);

    /**
     * 删除指定队列与组
     *
     * @param queue 队列名
     * @param group 组名
     * @return true: 表示删除成功
     */
    Boolean delete(String queue, String group);

    /**
     * 主动从队列中拉取消息
     *
     * @param queue 队列名
     * @return 消息
     */
    Message pull(String queue);

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
