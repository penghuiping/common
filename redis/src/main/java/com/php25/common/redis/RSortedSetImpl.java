package com.php25.common.redis;

import com.php25.common.core.util.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author penghuiping
 * @date 2020/1/9 13:12
 */
public class RSortedSetImpl<T> implements RSortedSet<T> {

    private StringRedisTemplate redisTemplate;

    private String key;


    public RSortedSetImpl(StringRedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public Boolean add(T t, double score) {
        return redisTemplate.opsForZSet().add(key, JsonUtil.toJson(t), score);
    }
}
