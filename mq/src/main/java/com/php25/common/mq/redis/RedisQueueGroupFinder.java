package com.php25.common.mq.redis;

import com.php25.common.mq.Message;
import com.php25.common.redis.RList;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;

/**
 * @author penghuiping
 * @date 2021/3/11 10:14
 */
class RedisQueueGroupFinder {

    private final RedisManager redisManager;

    public RedisQueueGroupFinder(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    /**
     * 获取系统中所有队列名
     *
     * @return 队列名
     */
    public RSet<String> queues() {
        return this.redisManager.set(RedisConstant.QUEUES, String.class);
    }

    /**
     * 根据队列名获取队列
     *
     * @param queue 队列名
     * @return 队列
     */
    public RList<Message> queue(String queue) {
        return this.redisManager.list(RedisConstant.QUEUE_PREFIX + queue, Message.class);
    }

    /**
     * 根据组名获取组
     *
     * @param group 组名
     * @return 组
     */
    public RList<Message> group(String group) {
        return this.redisManager.list(RedisConstant.GROUP_PREFIX + group, Message.class);
    }

    /**
     * 根据队列名获取绑定的组名
     *
     * @param queue
     * @return
     */
    public RSet<String> groups(String queue) {
        return this.redisManager.set(RedisConstant.QUEUE_GROUPS_PREFIX + queue, String.class);
    }
}
