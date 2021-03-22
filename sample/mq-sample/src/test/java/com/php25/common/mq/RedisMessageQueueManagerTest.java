package com.php25.common.mq;

import com.php25.common.core.util.RandomUtil;
import com.php25.common.mq.redis.RedisMessageQueueManager;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.impl.RedisManagerImpl;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

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
    public void before() throws Exception {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setHostName("localhost");
        redisStandaloneConfiguration.setPort(36379);
        redisStandaloneConfiguration.setPassword("");
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        connectionFactory.afterPropertiesSet();
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(connectionFactory);
        this.redisManager = new RedisManagerImpl(stringRedisTemplate);
        RedisMessageQueueManager redisMessageQueueManager = new RedisMessageQueueManager(redisManager);
        redisMessageQueueManager.afterPropertiesSet();
        this.messageQueueManager = redisMessageQueueManager;
        this.pool = Executors.newFixedThreadPool(10);
    }

    @After
    public void after() {
        this.messageQueueManager.delete("test", "Math");
        this.messageQueueManager.delete("test", "Chinese");
        this.messageQueueManager.delete("test0", "Math");
        this.messageQueueManager.delete("test0", "Chinese");
        this.messageQueueManager.delete("visitor");
        this.messageQueueManager.delete("price");
    }

    @Test
    public void test() throws Exception {
        int messageNum = 2;
        AtomicLong count = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(messageNum);

        messageQueueManager.subscribe("test", "Math", message -> {
            log.info("Math testers:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });
        messageQueueManager.subscribe("test", "Chinese", message -> {
            log.info("Chinese testers:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        pool.submit(() -> {
            messageQueueManager.send("test", new Message(RandomUtil.randomUUID(), "Math and Chinese has canceled today"));
        });
        countDownLatch.await();
        Assertions.assertThat(count.get()).isEqualTo(messageNum);
    }

    @Test
    public void test1() throws Exception {
        int messageNum = 1;
        AtomicLong count = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(messageNum);

        messageQueueManager.subscribe("test0", "Math", message -> {
            log.info("Math testers:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });
        messageQueueManager.subscribe("test0", "Chinese", message -> {
            log.info("Chinese testers:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        messageQueueManager.send("test0", "Math", new Message(RandomUtil.randomUUID(), "Math test has changed to be hold tomorrow"));
        countDownLatch.await();
        Assertions.assertThat(count.get()).isEqualTo(messageNum);
    }


    @Test
    public void test2() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        messageQueueManager.subscribe("visitor", message -> {
            log.info("消息体内容为:{}", message.getBody());
            countDownLatch.countDown();
        });
        messageQueueManager.send("visitor", new Message(RandomUtil.randomUUID(), "jack"));
        messageQueueManager.send("visitor", new Message(RandomUtil.randomUUID(), "mary"));
        messageQueueManager.send("visitor", new Message(RandomUtil.randomUUID(), "tom"));
        countDownLatch.await();
    }

    @Test
    public void test3() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        messageQueueManager.subscribe("price", message -> {
            log.info("消息体内容为:{}", message.getBody());
            Float.parseFloat(message.getBody().toString());
        });
        messageQueueManager.bindDeadLetterQueue("price");
        messageQueueManager.send("price", new Message(RandomUtil.randomUUID(), "1.0"));
        messageQueueManager.send("price", new Message(RandomUtil.randomUUID(), "一快二毛"));
        Message message1 = messageQueueManager.pullDlq("price", 5000L);
        log.info("dlq消息体内容为:{}", message1.getBody());
        countDownLatch.countDown();
        countDownLatch.await();
    }
}
