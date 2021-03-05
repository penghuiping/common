package com.php25.common.redis.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.TimeUtil;
import com.php25.common.redis.Person;
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

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/2/25 16:08
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RStringLocalTest {

    private static final Logger log = LoggerFactory.getLogger(RStringLocalTest.class);
    private RedisManager redisManager;

    @Before
    public void before() throws Exception {
        this.redisManager = new LocalRedisManager(1024);
        Person jack = new Person(30, "jack");
        Person mary = new Person(31, "mary");
        this.redisManager.string().set("person_jack", jack);
        this.redisManager.string().set("persons", Lists.newArrayList(jack, mary));
    }

    @Test
    public void remove() throws Exception {
        Person jack = this.redisManager.string().get("person_jack", Person.class);
        Assertions.assertThat(jack).isNotNull();
        this.redisManager.remove("person_jack");
        jack = this.redisManager.string().get("person_jack", Person.class);
        Assertions.assertThat(jack).isNull();
    }

    @Test
    public void exists() throws Exception {
        Boolean res = this.redisManager.exists("person_jack");
        Boolean res1 = this.redisManager.exists("person_mary");
        Assertions.assertThat(res).isEqualTo(true);
        Assertions.assertThat(res1).isEqualTo(false);
    }

    @Test
    public void expire() throws Exception {
        this.redisManager.expire("person_jack", 1L, TimeUnit.HOURS);
        Long expireTime = this.redisManager.getExpire("person_jack");
        Assertions.assertThat(expireTime).isGreaterThan(3598L).isLessThan(3601L);
    }

    @Test
    public void getExpire() throws Exception {
        this.redisManager.expire("person_jack", 10L, TimeUnit.SECONDS);
        Long expireTime = this.redisManager.getExpire("person_jack");
        Assertions.assertThat(expireTime).isGreaterThan(8L).isLessThan(11L);
    }

    @Test
    public void expireAt() throws Exception {
        Date date = TimeUtil.getEndTimeOfDay(new Date());
        this.redisManager.expireAt("person_jack", date);
        Long expireTime = this.redisManager.getExpire("person_jack");
        Long expireTime1 = TimeUnit.of(ChronoUnit.MILLIS).toSeconds(date.getTime() - System.currentTimeMillis());
        Assertions.assertThat(expireTime).isEqualTo(expireTime1);
    }

    @Test
    public void get() throws Exception {
        Person jack = this.redisManager.string().get("person_jack", Person.class);
        Assertions.assertThat(jack.getName()).isEqualTo("jack");
        Assertions.assertThat(jack.getAge()).isEqualTo(30);
        List<Person> persons = this.redisManager.string().get("persons", new TypeReference<List<Person>>() {
        });
        log.info("persons:{}", JsonUtil.toJson(persons));
        Assertions.assertThat(persons.size()).isEqualTo(2);
    }

    @Test
    public void set() throws Exception {
        Person jack = new Person(35, "jack");
        this.redisManager.string().set("person_jack", jack);
        jack = this.redisManager.string().get("person_jack", Person.class);
        Assertions.assertThat(jack.getName()).isEqualTo("jack");
        Assertions.assertThat(jack.getAge()).isEqualTo(35);
    }

    @Test
    public void setWithExpire() throws Exception {
        Person jack = new Person(35, "jack");
        this.redisManager.string().set("person_jack", jack, 60L);
        Long expireTime = this.redisManager.getExpire("person_jack");
        Assertions.assertThat(expireTime).isLessThanOrEqualTo(60L);
        Long expireTime1 = this.redisManager.getExpire("person_jack");
        Assertions.assertThat(expireTime1).isLessThanOrEqualTo(expireTime);
    }

    @Test
    public void setNx() throws Exception {
        Person jack = new Person(35, "jack");
        this.redisManager.string().setNx("person_jack", jack);
        jack = this.redisManager.string().get("person_jack", Person.class);
        Assertions.assertThat(jack.getAge()).isEqualTo(30);

        Person mary = new Person(31, "mary");
        this.redisManager.string().setNx("person_mary", mary);
        mary = this.redisManager.string().get("person_mary", Person.class);
        Assertions.assertThat(mary.getAge()).isEqualTo(31);
        Assertions.assertThat(mary.getName()).isEqualTo("mary");
    }

    @Test
    public void setNxWithExpire() throws Exception {
        Person mary = new Person(31, "mary");
        this.redisManager.string().setNx("person_mary", mary, 60L);
        Long expireTime = this.redisManager.getExpire("person_mary");
        Assertions.assertThat(expireTime).isLessThanOrEqualTo(60L);
        Long expireTime1 = this.redisManager.getExpire("person_mary");
        Assertions.assertThat(expireTime1).isLessThanOrEqualTo(expireTime);
    }


    @Test
    public void incr() throws Exception {
        log.info("start...");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            pool.submit(() -> {
                this.redisManager.string().incr("test");
                latch.countDown();
            });
        }
        latch.await();
        Long res = this.redisManager.string().get("test", Long.class);
        log.info("结果为:{}", res);
        Assertions.assertThat(res).isEqualTo(10000);
    }


    @Test
    public void decr() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10000);
        this.redisManager.string().set("test", 10000);
        for (int i = 0; i < 10000; i++) {
            pool.submit(() -> {
                this.redisManager.string().decr("test");
                latch.countDown();
            });
        }
        latch.await();
        Long res = this.redisManager.string().get("test", Long.class);
        System.out.println("结果为:" + res);
        Assertions.assertThat(res).isEqualTo(0);
    }

    @Test
    public void setBitTest() throws Exception {
        this.redisManager.string().setBit("test_set_bit", 3, true);//8L
        this.redisManager.string().setBit("test_set_bit", 2, true);//4L

        Assertions.assertThat(this.redisManager.string().getBit("test_set_bit", 3)).isTrue();
        Assertions.assertThat(this.redisManager.string().getBit("test_set_bit", 2)).isTrue();

        this.redisManager.string().setBit("test_set_bit", 3, false);
        Assertions.assertThat(this.redisManager.string().getBit("test_set_bit", 3)).isFalse();
        Assertions.assertThat(this.redisManager.string().getBit("test_set_bit", 2)).isTrue();
    }


}
