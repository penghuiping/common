package com.php25.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.dto.RedisLockInfo;
import com.php25.common.repository.impl.BaseRepositoryImpl;
import com.php25.common.service.ConsistentHashingService;
import com.php25.common.service.IdGeneratorService;
import com.php25.common.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest
@DataJpaTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@EntityScan(basePackages = {"com.php25"})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class RedisCommonTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisCommonTest.class);


    @Autowired
    IdGeneratorService idGeneratorService;

    @Qualifier("redisServiceSpring")
    @Autowired
    RedisService redisService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConsistentHashingService consistentHashingService;

    int count = 0;

    public void add() {
        RedisLockInfo redisLockInfo = redisService.tryLock("testKey", 60 * 1000, 10 * 1000000);
        if (null != redisLockInfo) {
            count++;
            redisService.releaseLock(redisLockInfo);
        }
    }

    @Test
    public void test() throws Exception {
//        redisService.remove("test");
//        for (int i = 0; i < 100; i++) {
//            Long result = redisService.incr("test");
//            System.out.println(result);
//        }
//
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(19000);
        for (int i = 0; i < 19000; i++) {
            executorService.submit(() -> {
                add();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println(this.count);
    }


    @Test
    public void consistentHashing() throws Exception {
        String serverIp = consistentHashingService.getServer("HELLOWORLD");
        System.out.println("serverIp:" + serverIp);
    }

    @Test
    public void idGeneratorService() throws Exception {
        logger.info("snowflake:" + idGeneratorService.getModelPrimaryKeyNumber());
        logger.info("uuid:" + idGeneratorService.getModelPrimaryKey());
    }

}
