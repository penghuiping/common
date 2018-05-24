package com.php25.common.config;

import com.php25.common.service.RedisService;
import com.php25.common.service.impl.RedisRedissonServiceImpl;
import com.php25.common.service.impl.RedisSpringBootServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Auther: penghuiping
 * @Date: 2018/5/24 21:32
 * @Description:
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("localhost:6379");
        config.useSingleServer().setConnectionMinimumIdleSize(1);
        config.useSingleServer().setConnectionPoolSize(5);
        config.useSingleServer().setDatabase(0);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Bean("redisServiceRedisson")
    @Primary
    public RedisService redisService(@Autowired RedissonClient redissonClient) {
        RedisService redisService = new RedisRedissonServiceImpl(redissonClient);
        return redisService;
    }

    @Bean("redisServiceSpring")
    public RedisService redisServiceSpring(@Autowired StringRedisTemplate stringRedisTemplate) {
        return new RedisSpringBootServiceImpl(stringRedisTemplate);
    }


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

}
