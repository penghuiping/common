package com.php25.common.redissample.config;

import com.php25.common.redis.RedisService;
import com.php25.common.redis.RedisSpringBootServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: penghuiping
 * @Date: 2018/5/24 21:32
 * @Description:
 */
@Configuration
public class RedisConfig {


    @Bean("redisServiceSpring")
    @Primary
    public RedisService redisServiceSpring(@Autowired StringRedisTemplate stringRedisTemplate) {
        return new RedisSpringBootServiceImpl(stringRedisTemplate);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setDatabase(0);
        redisConfiguration.setHostName("localhost");
        redisConfiguration.setPort(36379);
        return new JedisConnectionFactory(redisConfiguration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory,"redisRegisterKey");
    }

}
