package com.php25.common.redis.config;

import com.php25.common.redis.RedisManager;
import com.php25.common.redis.local.LocalRedisManager;
import org.springframework.context.annotation.Bean;

/**
 * @author penghuiping
 * @date 2022/7/24 18:52
 */
public class MockRedisManagerAutoConfiguration {

    @Bean
    public RedisManager redisManager1() {
        return new LocalRedisManager(1024);
    }
}
