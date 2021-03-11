package com.php25.common.mq.redis;

import com.php25.common.mq.Message;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.mq.MessageSubscriber;
import com.php25.common.redis.RList;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;

import java.util.Set;


/**
 * @author penghuiping
 * @date 2021/3/10 20:55
 */
public class RedisMessageQueueManager implements MessageQueueManager {

    private final RedisManager redisManager;

    public RedisMessageQueueManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public Boolean subscribe(String queue, String group, MessageSubscriber subscriber) {
        RSet<String> groups = this.redisManager.set(String.format("queue_groups:%s", queue), String.class);
        groups.add(group);
        subscriber.subscribe(queue, group);
        return true;
    }

    @Override
    public Boolean send(String queue, Message message) {
        RSet<String> groups = this.redisManager.set(String.format("queue_groups:%s", queue), String.class);
        Set<String> groups0 = groups.members();
        for (String group : groups0) {
            RList<Message> rList = this.redisManager.list(String.format("queue:%s:%s", queue, group), Message.class);
            rList.leftPush(message);
        }
        return true;
    }

    @Override
    public Message pull(String queue) {
        return null;
    }

    @Override
    public Boolean bindDeadLetterQueue(String queue, String dlq) {
        return null;
    }
}
