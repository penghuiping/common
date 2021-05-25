package com.php25.common.mq.redis;

import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.redis.RList;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;

/**
 * @author penghuiping
 * @date 2021/3/11 10:14
 */
class RedisQueueGroupHelper {

    private final RedisManager redisManager;

    public RedisQueueGroupHelper(RedisManager redisManager) {
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
        AssertUtil.hasText(queue, "queue不能为空");
        return this.redisManager.list(RedisConstant.QUEUE_PREFIX + queue, Message.class);
    }

    /**
     * 根据组名获取组
     *
     * @param group 组名
     * @return 组
     */
    public RList<Message> group(String group) {
        AssertUtil.hasText(group, "group不能为空");
        return this.redisManager.list(RedisConstant.GROUP_PREFIX + group, Message.class);
    }

    /**
     * 根据队列名获取绑定的组名
     *
     * @param queue 队列名
     * @return 队列与组关系
     */
    public RSet<String> groups(String queue) {
        AssertUtil.hasText(queue, "queue不能为空");
        return this.redisManager.set(RedisConstant.QUEUE_GROUPS_PREFIX + queue, String.class);
    }


    public Boolean remove(String queue) {
        return this.remove(queue, null);
    }

    public Boolean remove(String queue, String group) {
        AssertUtil.hasText(queue, "queue不能为空");
        if (StringUtil.isNotBlank(group)) {
            this.redisManager.remove(RedisConstant.GROUP_PREFIX + group);
            RSet<String> rSet = this.groups(queue);
            rSet.remove(group.substring(group.indexOf(":") + 1));
            return true;
        }

        RSet<String> groups = this.groups(queue);
        if (null != groups && null != groups.members()) {
            for (String group0 : groups.members()) {
                this.redisManager.remove(RedisConstant.GROUP_PREFIX + group0);
            }
            this.redisManager.remove(RedisConstant.QUEUE_GROUPS_PREFIX + queue);
            this.redisManager.remove(RedisConstant.QUEUE_PREFIX + queue);
            this.redisManager.remove(RedisConstant.QUEUE_DLQ_PREFIX + queue);
        }
        return true;
    }


    /**
     * 根据队列名获取死信队列
     * 如果没有绑定死信队列,则返回null
     *
     * @param queue 队列名
     * @return 死信队列
     */
    public RList<Message> dlq(String queue) {
        AssertUtil.hasText(queue, "queue不能为空");
        String dlq = this.redisManager.string().get(RedisConstant.QUEUE_DLQ_PREFIX + queue, String.class);
        if (StringUtil.isBlank(dlq)) {
            return null;
        }
        return this.redisManager.list(RedisConstant.DLQ_PREFIX + dlq, Message.class);
    }

    /**
     * 直接通过死信队列名获取队列
     *
     * @param dlq 死信队列名
     * @return 死信队列
     */
    public RList<Message> dlq0(String dlq) {
        AssertUtil.hasText(dlq, "dlq不能为空");
        return this.redisManager.list(RedisConstant.DLQ_PREFIX + dlq, Message.class);
    }

    /**
     * 给队列绑定死信队列
     *
     * @param queue 队列名
     * @param dlq   死信队列名
     * @return true: 绑定成功
     */
    public Boolean bindDlq(String queue, String dlq) {
        AssertUtil.hasText(queue, "queue不能为空");
        AssertUtil.hasText(dlq, "dlq不能为空");
        return this.redisManager.string().set(RedisConstant.QUEUE_DLQ_PREFIX + queue, dlq);
    }

    /**
     * 判断组是否有效
     *
     * @param group 组名
     * @return true 组有效
     */
    public Boolean groupIsValid(String group) {
        AssertUtil.hasText(group, "group不能为空");
        return this.redisManager.exists(RedisConstant.GROUP_PING_PREFIX + group);
    }
}
