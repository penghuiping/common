package com.php25.common.redis.local;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.RRateLimiter;
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

import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/2 17:38
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RRateLimiterLocalTest {

    private static final Logger log = LoggerFactory.getLogger(RRateLimiterLocalTest.class);

    private RedisManager redisManager;

    @Before
    public void before() {
        redisManager = new LocalRedisManager(1024);
    }

    @Test
    public void test() throws Exception {
        RRateLimiter rRateLimiter = redisManager.rateLimiter(1024, 4, "test_rl");
        Long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        Long count = 0L;
        while (expiredTime - System.currentTimeMillis() > 0) {
            if (rRateLimiter.isAllowed()) {
                count++;
            }
            Thread.sleep(100);
        }
        log.info("count:{}", count);
        Assertions.assertThat(count).isGreaterThan(18).isLessThan(22);

        RRateLimiter rRateLimiter1 = redisManager.rateLimiter(1024, 4, "test_rl");
        expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        count = 0L;
        while (expiredTime - System.currentTimeMillis() > 0) {
            if (rRateLimiter1.isAllowed()) {
                count++;
            }
            Thread.sleep(100);
        }
        log.info("count:{}", count);
        Assertions.assertThat(count).isGreaterThan(18).isLessThan(22);
    }
}
