package com.php25.common.mq.redis;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.mq.MessageSubscriber;
import com.php25.common.redis.RList;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author penghuiping
 * @date 2021/3/10 20:55
 */
public class RedisMessageQueueManager implements MessageQueueManager {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageQueueManager.class);

    private final RedisManager redisManager;

    private final ExecutorService singleThreadPool;

    private final RedisQueueGroupFinder finder;

    private final BlockingQueue<String> pipe;

    public RedisMessageQueueManager(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("redis_message_queue_manager_thread").build());
        this.pipe = new LinkedBlockingQueue<>();
        this.finder = new RedisQueueGroupFinder(this.redisManager);
        this.startWorker();
    }

    @Override
    public Boolean subscribe(String queue, String group, MessageSubscriber subscriber) {
        RSet<String> groups = this.finder.groups(queue);
        groups.add(group);
        subscriber.subscribe(queue, group);
        return true;
    }

    @Override
    public Boolean send(String queue, Message message) {
        this.finder.queue(queue).leftPush(message);
        this.pipe.offer(queue);
        return true;
    }

    private void startWorker() {
        this.singleThreadPool.submit(() -> {
            while (true) {
                try {
                    String queue = this.pipe.poll(60, TimeUnit.SECONDS);
                    if (!StringUtil.isBlank(queue)) {
                        Message message = this.pull(queue);
                        RSet<String> groups = this.finder.groups(queue);
                        Set<String> groups0 = groups.members();
                        for (String group : groups0) {
                            RList<Message> rList = this.finder.group(group);
                            rList.leftPush(message);
                        }
                    }
                } catch (Exception e) {
                    log.error("分发队列的消息给组出错!", e);
                }

            }
        });
    }

    @Override
    public Message pull(String queue) {
        return this.finder.queue(queue).rightPop();
    }

    @Override
    public Boolean bindDeadLetterQueue(String queue, String dlq) {
        return null;
    }

    @Override
    public List<String> queues() {
        return Lists.newArrayList(this.finder.queues().members());
    }
}
