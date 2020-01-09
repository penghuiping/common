package com.php25.common.redissample;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGenerator;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RHyperLogLogs;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.RedisManagerImpl;
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
    IdGenerator idGeneratorService;


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
        this.redisManager = new RedisManagerImpl(redisTemplate);
    }

    int count = 0;

    Long result = 0l;


    @Test
    public void distributeLock() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        Lock lock = redisManager.lock("test12333");
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
        CountDownLatch countDownLatch = new CountDownLatch(200);
        ExecutorService executorService = new ThreadPoolExecutor(20, 20,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2048));
        List<Callable<Integer>> list = Lists.newArrayList();
        for (int i = 0; i < 200; i++) {
            list.add(() -> {
                redisManager.string().incr("test");
                countDownLatch.countDown();
                logger.info("countdown" + countDownLatch.getCount());
                return 1;
            });
        }
        executorService.invokeAll(list);
        countDownLatch.await(10L, TimeUnit.SECONDS);
        result = redisManager.string().incr("test");
        Assertions.assertThat(result).isEqualTo(201);
    }


    @Test
    public void listTest() throws Exception {
        RListTest rListTest = new RListTest();
        rListTest.leftPushAndLeftPop(redisManager);
        rListTest.rightPushAndRightPop(redisManager);
        rListTest.leftRange(redisManager);
        rListTest.leftTrim(redisManager);
    }

    @Test
    public void bloomFilterTest() throws Exception {
        RBloomFilter bloomFilter = redisManager.bloomFilter("bf:test",1000,0.001d);

        for(int i=0;i<1000;i++) {
            bloomFilter.put(i+"");
        }

        Assertions.assertThat(bloomFilter.isExist("1")).isEqualTo(true);
        Assertions.assertThat(bloomFilter.isExist("22")).isEqualTo(true);
        Assertions.assertThat(bloomFilter.isExist("555")).isEqualTo(true);
        Assertions.assertThat(bloomFilter.isExist("1001")).isEqualTo(false);
    }

    @Test
    public void hyperLogLogsTests() throws Exception {
        RHyperLogLogs rHyperLogLogs = redisManager.hyperLogLogs("testSET11");
        String[] values = new String[1000];
        for(int i=0;i<1000;i++) {
           values[i] = i+"";
        }
        rHyperLogLogs.add(values);
        logger.info("大小为:{}",rHyperLogLogs.size());
    }




}
