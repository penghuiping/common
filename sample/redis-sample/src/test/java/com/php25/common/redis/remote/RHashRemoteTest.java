package com.php25.common.redis.remote;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.Person;
import com.php25.common.redis.RHash;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.impl.RedisManagerImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author penghuiping
 * @date 2021/3/4 10:32
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RHashRemoteTest {
    private static final Logger log = LoggerFactory.getLogger(RHashRemoteTest.class);
    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);
    private RHash<Person> rHash;
    private RedisManager redisManager;

    @Before
    public void before() throws Exception {
        String address = redis.getContainerIpAddress();
        Integer port = redis.getFirstMappedPort();
        //单机
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setDatabase(0);
        redisConfiguration.setHostName(address);
        redisConfiguration.setPort(port);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        this.redisManager = new RedisManagerImpl(new StringRedisTemplate(lettuceConnectionFactory));

        this.rHash = redisManager.hash("my_hash", Person.class);
        this.rHash.put("jack", new Person(12, "jack"));
        this.rHash.put("mary", new Person(13, "mary"));
    }

    @Test
    public void put() throws Exception {
        this.rHash.put("tom", new Person(11, "tom"));
        Assertions.assertThat(this.rHash.hasKey("tom")).isTrue();
    }

    @Test
    public void putIfAbsent() throws Exception {
        this.rHash.putIfAbsent("jack", new Person(11, "jack1"));
        Assertions.assertThat(this.rHash.get("jack").getName()).isEqualTo("jack").isNotEqualTo("jack1");

        this.rHash.putIfAbsent("tom", new Person(11, "tom"));
        Assertions.assertThat(this.rHash.hasKey("tom")).isTrue();
    }

    @Test
    public void get() throws Exception {
        Person jack = this.rHash.get("jack");
        Assertions.assertThat(jack).isEqualTo(new Person(12, "jack"));
    }

    @Test
    public void hasKey() throws Exception {
        Assertions.assertThat(this.rHash.hasKey("mary")).isTrue();
        Assertions.assertThat(this.rHash.hasKey("tom")).isFalse();
    }

    @Test
    public void delete() throws Exception {
        Assertions.assertThat(this.rHash.hasKey("mary")).isTrue();
        this.rHash.delete("mary");
        Assertions.assertThat(this.rHash.hasKey("mary")).isFalse();
    }

    @Test
    public void incr() throws Exception {
        log.info("start...");
        RHash<Long> rHash1 = redisManager.hash("my_hash1", Long.class);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            pool.submit(() -> {
                rHash1.incr("test_incr");
                latch.countDown();
            });
        }
        latch.await();
        Long res = rHash1.get("test_incr");
        log.info("结果为:{}", res);
        Assertions.assertThat(res).isEqualTo(10000);
    }

    @Test
    public void decr() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10000);
        RHash<Long> rHash1 = redisManager.hash("my_hash1", Long.class);
        rHash1.put("test_decr", 10000L);
        for (int i = 0; i < 10000; i++) {
            pool.submit(() -> {
                rHash1.decr("test_decr");
                latch.countDown();
            });
        }
        latch.await();
        Long res = rHash1.get("test_decr");
        System.out.println("结果为:" + res);
        Assertions.assertThat(res).isEqualTo(0);
    }
}
