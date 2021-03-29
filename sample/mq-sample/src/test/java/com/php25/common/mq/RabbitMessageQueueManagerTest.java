package com.php25.common.mq;

import com.php25.common.core.util.RandomUtil;
import com.php25.common.mq.rabbit.RabbitMessageListener;
import com.php25.common.mq.rabbit.RabbitMessageQueueManager;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author penghuiping
 * @date 2021/3/20 19:57
 */
public class RabbitMessageQueueManagerTest {

    private static final Logger log = LoggerFactory.getLogger(RabbitMessageQueueManagerTest.class);

    private CachingConnectionFactory connectionFactory;

    private MessageQueueManager messageQueueManager;

    private RabbitTemplate rabbitTemplate;

    private ExecutorService pool;

    @Before
    public void before() throws Exception {
        this.pool = Executors.newFixedThreadPool(10);

        this.connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);

        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
        RabbitMessageListener listener = new RabbitMessageListener(this.rabbitTemplate);
        RabbitMessageQueueManager rabbitMessageQueueManager = new RabbitMessageQueueManager(this.rabbitTemplate, listener);
        rabbitMessageQueueManager.afterPropertiesSet();
        this.messageQueueManager = rabbitMessageQueueManager;
    }

    @After
    public void after() throws Exception {
        this.messageQueueManager.delete("test", "Math");
        this.messageQueueManager.delete("test", "Chinese");
        this.messageQueueManager.delete("test0", "Math0");
        this.messageQueueManager.delete("test0", "Chinese0");
        this.messageQueueManager.delete("test");
        this.messageQueueManager.delete("test0");

        this.messageQueueManager.delete("visitor");
        this.messageQueueManager.delete("price");
        RabbitMessageQueueManager rabbitMessageQueueManager = (RabbitMessageQueueManager) this.messageQueueManager;
        rabbitMessageQueueManager.destroy();
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
        int messageNum = 10;
        AtomicLong count = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(messageNum);

        messageQueueManager.subscribe("test0", "Math0", message -> {
            log.info("Math testers:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        messageQueueManager.subscribe("test0", "Math0", message -> {
            log.info("Math testers1:{}", message.getBody());
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        for (int i = 0; i < messageNum; i++) {
            messageQueueManager.send("test0", "Math0", new Message(RandomUtil.randomUUID(), "Math test has changed to be hold tomorrow"));
        }
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
