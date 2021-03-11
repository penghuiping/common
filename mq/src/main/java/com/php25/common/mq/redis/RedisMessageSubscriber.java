package com.php25.common.mq.redis;

import com.php25.common.mq.Message;
import com.php25.common.mq.MessageHandler;
import com.php25.common.mq.MessageSubscriber;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author penghuiping
 * @date 2021/3/10 20:32
 */
public class RedisMessageSubscriber implements MessageSubscriber {

    private final static Logger log = LoggerFactory.getLogger(RedisMessageSubscriber.class);

    private final RedisManager redisManager;
    private final RedisQueueGroupFinder finder;

    private final ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private MessageHandler handler;
    private Future<?> threadFuture;
    private RList<Message> group;


    public RedisMessageSubscriber(ExecutorService executorService, RedisManager redisManager) {
        this.executorService = executorService;
        this.redisManager = redisManager;
        this.finder = new RedisQueueGroupFinder(this.redisManager);
    }

    @Override
    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void subscribe(String queue, String group) {
        this.group = this.finder.group(group);
        this.start();
    }

    public void stop() {
        isRunning.compareAndSet(true, false);
    }

    public void start() {
        if (null == this.threadFuture || this.threadFuture.isDone()) {
            synchronized (this) {
                if (null == this.threadFuture || this.threadFuture.isDone()) {
                    this.threadFuture = executorService.submit(() -> {
                        while (isRunning.get()) {
                            try {
                                Message message0 = group.blockRightPop(1, TimeUnit.SECONDS);
                                if (null != message0) {
                                    this.handler.handle(message0);
                                }
                            } catch (Exception e) {
                                log.error("MessageSubscriber消费消息出错", e);
                            }
                        }
                        log.info("回收mq-subscriber线程");
                    });
                }
            }
        }
    }
}
