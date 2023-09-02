package com.php25.common.redis.config;

import com.google.common.collect.Sets;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

/**
 * @author penghuiping
 * @date 2022/7/24 18:49
 */
@EnableConfigurationProperties({RedisProperties.class})
public class SentinelRedisConfig {

    @Bean(destroyMethod = "destroy")
    public LettuceConnectionFactory redisConnectionFactory2(RedisProperties redisProperties, ClientResources lettuceClientResources) {
        RedisProperties.Sentinel sentinelProperties = redisProperties.getSentinel();
        RedisSentinelConfiguration config = new RedisSentinelConfiguration(
                sentinelProperties.getMaster(), Sets.newHashSet(sentinelProperties.getNodes()));

        if (sentinelProperties.getPassword() != null) {
            config.setSentinelPassword(sentinelProperties.getPassword());
        }

        if (redisProperties.getPassword() != null) {
            config.setPassword(redisProperties.getPassword());
        }

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();

        if (redisProperties.getSsl().isEnabled()) {
            builder.useSsl();
        }

        RedisProperties.Pool properties = redisProperties.getLettuce().getPool();
        LettuceClientConfiguration clientConfiguration = builder
                .commandTimeout((redisProperties.getTimeout()))
                .clientResources(lettuceClientResources)
                .clientOptions(ClientOptions.builder()
                        .socketOptions(SocketOptions.builder().connectTimeout(redisProperties.getConnectTimeout()).build())
                        .timeoutOptions(TimeoutOptions.enabled()).build())
                .poolConfig(getPoolConfig(properties)).build();
        return new LettuceConnectionFactory(config, clientConfiguration);
    }

    private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
        }
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }
}
