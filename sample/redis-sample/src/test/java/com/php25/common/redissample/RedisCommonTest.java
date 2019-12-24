package com.php25.common.redissample;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.RedisSpringBootManagerImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RedisCommonTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisCommonTest.class);

    @Autowired
    IdGeneratorService idGeneratorService;


    RedisManager redisManager;

    StringRedisTemplate redisTemplate;

    RedisConnectionFactory redisConnectionFactory;

    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);

    @Before
    public void setUp() {
        String address = redis.getContainerIpAddress();
        Integer port = redis.getFirstMappedPort();

        //单机
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setDatabase(0);
        redisConfiguration.setHostName(address);
        redisConfiguration.setPort(port);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();

        //集群
//        RedisProperties.Cluster clusterProperties = new RedisProperties.Cluster();
//        clusterProperties.setNodes();
//        clusterProperties.setMaxRedirects();
//        RedisClusterConfiguration config = new RedisClusterConfiguration(
//                clusterProperties.getNodes());
//
//        if (clusterProperties.getMaxRedirects() != null) {
//            config.setMaxRedirects(clusterProperties.getMaxRedirects());
//        }
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
//        lettuceConnectionFactory.afterPropertiesSet();

        this.redisConnectionFactory = lettuceConnectionFactory;
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        this.redisManager = new RedisSpringBootManagerImpl(redisTemplate);
    }

    int count = 0;

    Long result = 0l;


    @Test
    public void distributeLock() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        Lock lock = redisManager.obtainDistributeLock("test12333");
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                long start = System.currentTimeMillis();
                try {
                    lock.lock();
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                    countDownLatch.countDown();
                    logger.info("count:{},耗时:{},countDown:{}", count, System.currentTimeMillis() - start, countDownLatch.getCount());
                }
            });
        }
        countDownLatch.await();
        Assertions.assertThat(this.count).isEqualTo(1000);
    }

    @Test
    public void incr() throws Exception {
        redisManager.remove("test");
        CountDownLatch countDownLatch = new CountDownLatch(200);
        ExecutorService executorService = new ThreadPoolExecutor(20, 20,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2048));
        List<Callable<Integer>> list = Lists.newArrayList();
        for (int i = 0; i < 200; i++) {
            list.add(() -> {
                redisManager.incr("test");
                countDownLatch.countDown();
                logger.info("countdown" + countDownLatch.getCount());
                return 1;
            });
        }
        executorService.invokeAll(list);
        countDownLatch.await(10L, TimeUnit.SECONDS);
        result = redisManager.incr("test");
        Assertions.assertThat(result).isEqualTo(201);
    }

}
