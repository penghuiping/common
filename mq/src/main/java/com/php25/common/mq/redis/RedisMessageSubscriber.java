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
    private MessageHandler handler;
    private Future<?> threadFuture;
    private RList<Message> pipe;


    public RedisMessageSubscriber(ExecutorService executorService,
                                  RedisManager redisManager) {
        this.executorService = executorService;
        this.helper = new RedisQueueGroupHelper(redisManager);
    }

    @Override
    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void subscribe(String queue, String group) {
        if (!StringUtil.isBlank(queue)) {
            this.pipe = this.helper.queue(queue);
        }

        if (!StringUtil.isBlank(group)) {
            this.pipe = this.helper.group(group);
        }
        this.start();
    }

    @Override
    public void subscribe(String queue) {
        this.subscribe(queue, null);
    }

    public void stop() {
        isRunning.compareAndSet(true, false);
    }

    public boolean isStop() {
        return !isRunning.get();
    }

    public void start() {
        if (null == this.threadFuture || this.threadFuture.isDone()) {
            synchronized (this) {
                if (null == this.threadFuture || this.threadFuture.isDone()) {
                    this.threadFuture = executorService.submit(() -> {
                        while (isRunning.get()) {
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
