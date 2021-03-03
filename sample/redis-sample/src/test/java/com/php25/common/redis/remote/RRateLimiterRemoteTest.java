package com.php25.common.redis.remote;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.RRateLimiter;
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

import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/3 21:05
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RRateLimiterRemoteTest {

    private static final Logger log = LoggerFactory.getLogger(RRateLimiterRemoteTest.class);
    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);
    private RedisManager redisManager;

    @Before
    public void before() {
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
