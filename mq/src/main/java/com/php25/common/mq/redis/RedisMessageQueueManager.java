package com.php25.common.mq.redis;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageHandler;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.redis.RList;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * redis版本消息队列实现
 * <p>
 * （注: 此版本实现存在丢消息风险，如果对消息可达性有强制要求,请选择Rabbit、Kafka、Rocket版本）
 *
 * @author penghuiping
 * @date 2021/3/10 20:55
 */
public class RedisMessageQueueManager implements MessageQueueManager, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageQueueManager.class);

    private final RedisManager redisManager;


    private final RedisQueueGroupHelper helper;

    private final BlockingQueue<String> pipe;

    private final List<RedisMessageSubscriber> subscribers;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService singleThreadPool;
    private ExecutorService subscriberThreadPool;

    public RedisMessageQueueManager(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.pipe = new LinkedBlockingQueue<>();
        this.helper = new RedisQueueGroupHelper(this.redisManager);
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("redis-message-queue-manager-thread").build());
        this.subscriberThreadPool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("redis-subscriber-thread-%d").build());
        this.startWorker();
    }

    @Override
    public void destroy() throws Exception {
        for (RedisMessageSubscriber subscriber : this.subscribers) {
            subscriber.stop();
        }
        this.isRunning.compareAndSet(true, false);

        try {
            this.subscriberThreadPool.shutdown();
            boolean res = this.subscriberThreadPool.awaitTermination(3, TimeUnit.SECONDS);
            if (res) {
                log.info("mq:subscriberThreadPool关闭成功");
            }
        } catch (Exception e) {
            log.error("mq:subscriberThreadPool关闭出错", e);
            Thread.currentThread().interrupt();
        }

        try {
            this.singleThreadPool.shutdown();
            boolean res = this.singleThreadPool.awaitTermination(3, TimeUnit.SECONDS);
            if (res) {
                log.info("mq:RedisMessageQueueManager:singleThreadPool关闭成功");
            }
        } catch (Exception e) {
            log.error("mq:RedisMessageQueueManager:singleThreadPool关闭出错", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Boolean subscribe(String queue, MessageHandler handler) {
        return this.subscribe(queue, queue, handler);
    }

    @Override
    public Boolean subscribe(String queue, String group, MessageHandler handler) {
        return this.subscribe(queue, group, false, handler);
    }

    @Override
    public Boolean subscribe(String queue, String group, Boolean autoDelete, MessageHandler handler) {
        AssertUtil.hasText("queue", "queue不能为空");
        AssertUtil.hasText("group", "group不能为空");
        RSet<String> groups = this.helper.groups(queue);
        String group0 = this.groupName(queue, group);
        groups.add(group0);
        RedisMessageSubscriber subscriber = new RedisMessageSubscriber(subscriberThreadPool, redisManager, autoDelete);
        subscriber.subscribe(queue, group0);
        subscriber.setHandler(handler);
        this.subscribers.add(subscriber);
        return true;
    }

    private String groupName(String queue, String group) {
        return queue + ":" + group;
    }

    @Override
    public Boolean send(String queue, Message message) {
        return this.send(queue, null, message);
    }


    @Override
    public Boolean delete(String queue) {
        return this.delete(queue, null);
    }

    @Override
    public Boolean delete(String queue, String group) {
        if (StringUtil.isBlank(group)) {
            return this.helper.remove(queue);
        } else {
            return this.helper.remove(queue, this.groupName(queue, group));
        }
    }

    private void startWorker() {
        this.isRunning.compareAndSet(false, true);
        this.singleThreadPool.submit(() -> {
            while (this.isRunning.get()) {
                try {
                    String queue = this.pipe.poll(1, TimeUnit.SECONDS);
                    if (!StringUtil.isBlank(queue)) {
                        RSet<String> groups = this.helper.groups(queue);
                        if (groups.size() > 0) {
                            Message message = this.pull(queue);
                            Set<String> groups0 = groups.members();
                            for (String group : groups0) {
                                if (!this.helper.groupIsValid(group)) {
                                    groups.remove(group);
                                    continue;
                                }
                                RList<Message> rList = this.helper.group(group);
                                rList.leftPush(message);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("分发队列的消息给组出错!", e);
                }
            }
            log.info("mq:RedisMessageQueueManager:singleThreadPool回收");
        });
    }

    private Message pull(String queue) {
        return this.helper.queue(queue).rightPop();
    }

    @Override
    public Message pullDlq(String queue, Long timeout) {
        return this.helper.dlq(queue).blockRightPop(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean bindDeadLetterQueue(String queue) {
        return this.helper.bindDlq(queue, queue + "_dlq");
    }

    @Override
    public Boolean send(String queue, String group, Message message) {
        message.setQueue(queue);
        message.setGroup(group);
        if (!StringUtil.isBlank(group)) {
            if (!this.helper.groupIsValid(this.groupName(queue, group))) {
                return false;
            }
            RList<Message> messages = this.helper.group(this.groupName(queue, group));
            messages.leftPush(message);
            return true;
        } else {
            this.helper.queue(queue).leftPush(message);
            this.pipe.offer(queue);
            return true;
        }
    }
}
