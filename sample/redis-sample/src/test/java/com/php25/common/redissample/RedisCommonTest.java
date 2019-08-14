package com.php25.common.redissample;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.redis.RedisService;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Qualifier("redisServiceSpring")
    @Autowired
    RedisService redisService;


    int count = 0;

    Long result = 0l;

    public void add() {
        Lock lock = redisService.obtainDistributeLock("test");
        try {
            lock.lock();
            count++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void distributeLock() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(2000);
        for (int i = 0; i < 2000; i++) {
            executorService.submit(() -> {
                add();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Assert.assertEquals(this.count, 2000);
    }

    @Test
    public void incr() throws Exception {
        redisService.remove("test");
        CountDownLatch countDownLatch = new CountDownLatch(2000);
        ExecutorService executorService = new ThreadPoolExecutor(20, 20,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2048));
        List list = Lists.newArrayList();
        for (int i = 0; i < 2000; i++) {
            list.add((Callable) () -> {
                redisService.incr("test");
                countDownLatch.countDown();
                return 1;
            });
        }
        executorService.invokeAll(list);
        countDownLatch.await(10L, TimeUnit.SECONDS);
        result = redisService.incr("test");
        Assert.assertEquals(result, new Long(2001));
    }

}
