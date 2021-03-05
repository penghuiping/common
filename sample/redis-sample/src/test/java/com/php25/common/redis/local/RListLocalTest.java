package com.php25.common.redis.local;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.redis.Person;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author penghuiping
 * @date 2021/3/2 17:37
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RListLocalTest {
    private static final Logger log = LoggerFactory.getLogger(RListLocalTest.class);
    private RList<Person> rList;
    private RedisManager redisManager;


    @Before
    public void before() throws Exception {
        this.redisManager = new LocalRedisManager(1024);
        this.rList = redisManager.list("my_list", Person.class);
        this.rList.leftPush(new Person(12, "jack"));
        this.rList.leftPush(new Person(13, "mary"));
    }

    @Test
    public void rightPush() throws Exception {
        Person tom = new Person(11, "tom");
        Long res = this.rList.rightPush(tom);
        Assertions.assertThat(res).isEqualTo(3);
        Person tom1 = this.rList.rightPop();
        Assertions.assertThat(tom1).isEqualTo(tom);
    }

    @Test
    public void leftPush() throws Exception {
        Person tom = new Person(11, "tom");
        Long res = this.rList.leftPush(tom);
        Assertions.assertThat(res).isEqualTo(3);
        Person tom1 = this.rList.leftPop();
        Assertions.assertThat(tom1).isEqualTo(tom);
    }

    @Test
    public void rightPop() throws Exception {
        Person right = this.rList.rightPop();
        Person jack = new Person(12, "jack");
        Assertions.assertThat(right).isEqualTo(jack);
    }

    @Test
    public void leftPop() throws Exception {
        Person right = this.rList.leftPop();
        Person mary = new Person(13, "mary");
        Assertions.assertThat(right).isEqualTo(mary);
    }

    @Test
    public void leftRange() throws Exception {
        //alice tom mary jack
        Person tom = new Person(11, "tom");
        this.rList.leftPush(tom);
        Person alice = new Person(10, "alice");
        this.rList.leftPush(alice);

        List<Person> list = this.rList.leftRange(1, rList.size());
        log.info("list:{}", JsonUtil.toJson(list));
        Assertions.assertThat(list.size()).isEqualTo(3);
        Assertions.assertThat(list.contains(alice)).isFalse();
        Assertions.assertThat(list.contains(tom)).isTrue();
        Assertions.assertThat(this.rList.size()).isEqualTo(4);
    }

    @Test
    public void leftTrim() throws Exception {
        //alice tom mary jack
        Person tom = new Person(11, "tom");
        this.rList.leftPush(tom);
        Person alice = new Person(10, "alice");
        this.rList.leftPush(alice);

        this.rList.leftTrim(1, rList.size());
        Assertions.assertThat(this.rList.size()).isEqualTo(3);
        List<Person> list = this.rList.leftRange(0, rList.size());
        Assertions.assertThat(list.contains(alice)).isFalse();
        Assertions.assertThat(list.contains(tom)).isTrue();
    }

    @Test
    public void size() throws Exception {
        Assertions.assertThat(this.rList.size()).isEqualTo(2);
    }

    @Test
    public void blockLeftPop() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                this.rList.leftPush(new Person(i, RandomUtil.randomUUID()));
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("error:", e);
                }
            }
            countDownLatch.countDown();
        });

        AtomicLong count = new AtomicLong();

        //hold 5秒
        Thread consumer = new Thread(() -> {
            long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L);
            while (System.currentTimeMillis() < expiredTime) {
                Person person = this.rList.blockLeftPop(1, TimeUnit.SECONDS);
                if (person == null) {
                    log.info("无结果1秒超时释放");
                } else {
                    count.getAndIncrement();
                    log.info("person:{}", JsonUtil.toJson(person));
                }
            }
            countDownLatch.countDown();
        });
        producer.start();
        consumer.start();
        countDownLatch.await();
        Assertions.assertThat(count.get()).isEqualTo(7);
    }

    @Test
    public void blockRightPop() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                this.rList.leftPush(new Person(i, RandomUtil.randomUUID()));
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("error:", e);
                }
            }
            countDownLatch.countDown();
        });

        AtomicLong count = new AtomicLong();

        //hold 5秒
        Thread consumer = new Thread(() -> {
            long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L);
            while (System.currentTimeMillis() < expiredTime) {
                Person person = this.rList.blockRightPop(1, TimeUnit.SECONDS);
                if (person == null) {
                    log.info("无结果1秒超时释放");
                } else {
                    count.getAndIncrement();
                    log.info("person:{}", JsonUtil.toJson(person));
                }
            }
            countDownLatch.countDown();
        });
        producer.start();
        consumer.start();
        countDownLatch.await();
        Assertions.assertThat(count.get()).isEqualTo(7);
    }
}
