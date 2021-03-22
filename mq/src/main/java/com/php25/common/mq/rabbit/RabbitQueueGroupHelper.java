package com.php25.common.mq.rabbit;

/**
 * @author penghuiping
 * @date 2021/3/20 20:52
 */
class RabbitQueueGroupHelper {

    static String getDirectQueueName(String queue) {
        return "direct_queue:" + queue;
    }

    static String getFanoutQueueName(String queue) {
        return "fanout_queue:" + queue;
    }

    static String getGroupName(String group) {
        return "group:" + group;
    }

    static String getQueue(String queueName) {
        return queueName.substring(queueName.indexOf(":") + 1);
    }

    static String getDlq(String queue) {
        return getGroupName(queue + "_dlq");
    }

    static String getGroup(String groupName) {
        return groupName.substring(groupName.indexOf(":") + 1);
    }
}
