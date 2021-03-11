package com.php25.common.mq;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.mq.redis.RedisMessageQueueManager;
import com.php25.common.mq.redis.RedisMessageSubscriber;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.local.LocalRedisManager;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author penghuiping
 * @date 2021/3/11 09:09
 */
public class RedisMessageQueueManagerTest {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageQueueManagerTest.class);

    private RedisManager redisManager;

    private MessageQueueManager messageQueueManager;

    private ExecutorService pool;

    @Before
    public void before() {
        this.redisManager = new LocalRedisManager(1024);
        this.messageQueueManager = new RedisMessageQueueManager(redisManager);
        this.pool = Executors.newFixedThreadPool(10);
    }

    @Test
    public void test() throws Exception {
        int messageNum = 10;
        AtomicLong count = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(messageNum);
        MessageSubscriber messageSubscriber0 = new RedisMessageSubscriber(pool, redisManager);
        messageSubscriber0.setHandler(message -> {
            log.info("messageSubscriber0:{}", JsonUtil.toJson(message));
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        MessageSubscriber messageSubscriber1 = new RedisMessageSubscriber(pool, redisManager);
        messageSubscriber1.setHandler(message -> {
            log.info("messageSubscriber1:{}", JsonUtil.toJson(message));
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        MessageSubscriber messageSubscriber2 = new RedisMessageSubscriber(pool, redisManager);
        messageSubscriber2.setHandler(message -> {
            log.info("messageSubscriber2:{}", JsonUtil.toJson(message));
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        MessageSubscriber messageSubscriber3 = new RedisMessageSubscriber(pool, redisManager);
        messageSubscriber3.setHandler(message -> {
            log.info("messageSubscriber3:{}", JsonUtil.toJson(message));
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        messageQueueManager.subscribe("test_queue", "test_group_0", messageSubscriber0);
        messageQueueManager.subscribe("test_queue", "test_group_1", messageSubscriber1);
        messageQueueManager.subscribe("test_queue", "test_group_1", messageSubscriber2);
        messageQueueManager.subscribe("test_queue", "test_group_0", messageSubscriber3);

        pool.submit(() -> {
            for (int i = 0; i < messageNum; i++) {
                messageQueueManager.send("test_queue", new Message(i + "", "test_queue", "hello world:" + i));
                try {
                    Thread.sleep(1000);
                    log.info("==============");
                } catch (Exception e) {

                }
            }
        });
        countDownLatch.await();
        Assertions.assertThat(count.get()).isEqualTo(messageNum);
    }
}
