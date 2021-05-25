package com.php25.common.mq.redis;

import com.google.common.base.Charsets;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageHandler;
import com.php25.common.mq.MessageSubscriber;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
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

    private final RedisQueueGroupHelper helper;
    private final ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Boolean autoDelete;
    private MessageHandler handler;
    private Future<?> threadFuture;
    private RList<Message> pipe;
    private String group;
    private String queue;
    private final RedisManager redisManager;

    public RedisMessageSubscriber(ExecutorService executorService,
                                  RedisManager redisManager, Boolean autoDelete) {
        this.executorService = executorService;
        this.helper = new RedisQueueGroupHelper(redisManager);
        this.autoDelete = autoDelete;
        this.redisManager = redisManager;
    }

    @Override
    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void subscribe(String queue, String group) {
        this.queue = queue;
        this.group = group;
        this.pipe = this.helper.group(group);
        this.start();
    }

    @Override
    public void subscribe(String queue) {
        this.subscribe(queue, queue + ":" + queue);
    }

    private String groupName(String queue, String group) {
        return queue + ":" + group;
    }

    public void stop() {
        isRunning.compareAndSet(true, false);
        if (autoDelete) {
            if (StringUtil.isBlank(group)) {
                this.helper.remove(queue);
            } else {
                this.helper.remove(queue, this.groupName(queue, group));
            }
        }
    }

    public void start() {
        if (null == this.threadFuture || this.threadFuture.isDone()) {
            synchronized (this) {
                if (null == this.threadFuture || this.threadFuture.isDone()) {
                    this.threadFuture = executorService.submit(() -> {
                        int count = 0;

                        this.redisManager.string().set(RedisConstant.GROUP_PING_PREFIX + group, 1);

                        if (autoDelete) {
                            this.redisManager.expire(RedisConstant.GROUP_PREFIX + group, 60L, TimeUnit.SECONDS);
                            this.redisManager.expire(RedisConstant.GROUP_PING_PREFIX + group, 60L, TimeUnit.SECONDS);
                            this.redisManager.expire(RedisConstant.QUEUE_GROUPS_PREFIX + queue, 60L, TimeUnit.SECONDS);
                        }

                        while (isRunning.get()) {
                            if (autoDelete && count > 5) {
                                this.redisManager.expire(RedisConstant.GROUP_PREFIX + group, 60L, TimeUnit.SECONDS);
                                this.redisManager.expire(RedisConstant.GROUP_PING_PREFIX + group, 60L, TimeUnit.SECONDS);
                                this.redisManager.expire(RedisConstant.QUEUE_GROUPS_PREFIX + queue, 60L, TimeUnit.SECONDS);
                                count = 0;
                            }
                            count++;
                            try {
                                Message message0 = pipe.blockRightPop(1, TimeUnit.SECONDS);
                                if (null != message0) {
                                    try {
                                        this.handler.handle(message0);
                                    } catch (Exception e) {
                                        log.error("处理消息出错,msgId:{}", message0.getId(), e);
                                        RList<Message> dlq = this.helper.dlq(message0.getQueue());
                                        if (null != dlq) {
                                            try (
                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    PrintWriter writer = new PrintWriter(byteArrayOutputStream)
                                            ) {
                                                e.printStackTrace(writer);
                                                String error = byteArrayOutputStream.toString(Charsets.UTF_8.name());
                                                message0.setErrorInfo(error);
                                                dlq.leftPush(message0);
                                            }
                                        }
                                    }
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
