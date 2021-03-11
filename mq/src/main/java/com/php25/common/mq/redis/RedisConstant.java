package com.php25.common.mq.redis;

/**
 * @author penghuiping
 * @date 2021/3/11 10:04
 */
class RedisConstant {
    public static final String GROUP_PREFIX = "mq:group:";
    public static final String QUEUE_PREFIX = "mq:queue:";
    public static final String QUEUE_GROUPS_PREFIX = "mq:queue:groups:";
    public static final String QUEUES = "mq:queues";
    public static final String MESSAGES_CACHE = "mq:messages_cache:";
    public static final String QUEUE_DLQ_PREFIX = "mq:queue:dlq:";
    public static final String DLQ_PREFIX = "mq:dlq:";
    public static final String DLQ_DEFAULT = "mq:dlq:default";
}
