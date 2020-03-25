package com.php25.common.redis;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/3/25 14:44
 */
public class RRateLimiterImpl implements RRateLimiter {

    private StringRedisTemplate redisTemplate;

    private RedisScript<List<Long>> script;

    /**
     * 令牌桶最大容量
     */
    private int capacity;

    /**
     * 令牌增长率，单位时间内向令牌桶里添加令牌的数量
     */
    private int rate;

    /**
     * 令牌的唯一标识
     */
    private String id;

    public RRateLimiterImpl(StringRedisTemplate redisTemplate, int capacity, int rate, String id) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        this.script = redisScript;
        this.capacity = capacity;
        this.rate = rate;
        this.id = id;
    }

    /**
     * 使用令牌桶算法
     */
    @Override
    public Boolean isAllowed() {
        List<String> keys = getKeys(id);
        List<Long> result = this.redisTemplate.execute(this.script, keys, rate + "", capacity + "", Instant.now().getEpochSecond() + "", "1");
        return result.get(0) == 1;
    }

    private static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
