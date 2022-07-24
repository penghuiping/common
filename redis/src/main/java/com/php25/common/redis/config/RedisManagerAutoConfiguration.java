package com.php25.common.redis.config;

import brave.Tracing;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.impl.RedisManagerImpl;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.tracing.BraveTracing;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author penghuiping
 * @date 2022/7/24 18:30
 */
public class RedisManagerAutoConfiguration {

    @ConditionalOnMissingBean(ClientResources.class)
    @Bean(destroyMethod = "shutdown")
    ClientResources lettuceClientResources() {
        return DefaultClientResources.create().mutate()
                .tracing(BraveTracing.builder().serviceName("redis-lettuce").tracing(Tracing.current()).build()).build();
    }

    @Bean
    public RedisManager redisManager(StringRedisTemplate stringRedisTemplate) {
        return new RedisManagerImpl(stringRedisTemplate);
    }


    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
