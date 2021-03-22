package com.php25.common.mq;


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
     * @param queue   队列名
     * @param handler 消费者回调函数
     * @return true:订阅关系绑定成功
     */
    Boolean subscribe(String queue, MessageHandler handler);


    /**
     * 订阅(队列名:消费组名)
     *
     * @param queue   队列名
     * @param group   消费组名
     * @param handler 消费者回调函数
     * @return true:订阅关系绑定成功
     */
    Boolean subscribe(String queue, String group, MessageHandler handler);

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
     * 删除指定队列
     *
     * @param queue 队列名
     * @return true: 表示删除成功
     */
    Boolean delete(String queue);

    /**
     * 删除指定队列与组
     *
     * @param queue 队列名
     * @param group 组名
     * @return true: 表示删除成功
     */
    Boolean delete(String queue, String group);

    /**
     * 主动从死信队列中拉取消息
     *
     * @param queue   原队列名
     * @param timeout 超时时间单位(毫秒)
     * @return 消息
     */
    Message pullDlq(String queue, Long timeout);

    /**
     * 给某个队列绑定死信队列
     * 绑定的死信队列为  (原队列名_dlq)
     *
     * @param queue 队列名
     * @return true: 绑定成功
     */
    Boolean bindDeadLetterQueue(String queue);
}
