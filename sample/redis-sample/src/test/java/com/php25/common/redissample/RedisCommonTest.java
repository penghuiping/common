package com.php25.common.redissample;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.redis.RedisLockInfo;
import com.php25.common.redis.RedisService;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        RedisLockInfo redisLockInfo = redisService.tryLock("testKey", 60 * 1000, 30 * 1000);
        if (null != redisLockInfo) {
            count++;
            redisService.releaseLock(redisLockInfo);
        }
    }

    @Test
    public void test() throws Exception {
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
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(2000);
        for (int i = 0; i < 2000; i++) {
            executorService.submit(() -> {
                redisService.incr("test");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        result = redisService.incr("test");
        Assert.assertEquals(result, new Long(2001));
    }

}
