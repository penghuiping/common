package com.php25.common.redis.impl;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RSortedSet;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/1/9 13:12
 */
public class RSortedSetImpl<T> implements RSortedSet<T> {

    private final StringRedisTemplate redisTemplate;

    private final String key;

    private final Class<T> model;

    public RSortedSetImpl(StringRedisTemplate redisTemplate, String key, Class<T> model) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.model = model;
    }

    @Override
    public Boolean add(T t, double score) {
        return redisTemplate.opsForZSet().add(key, JsonUtil.toJson(t), score);
    }

    @Override
    public Set<T> range(long start, long end) {
        Set<String> result = redisTemplate.opsForZSet().range(key, start, end);
        return transfer(result);
    }

    @Override
    public Set<T> reverseRange(long start, long end) {
        Set<String> result = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return transfer(result);
    }

    @Override
    public Set<T> rangeByScore(double min, double max) {
        Set<String> result = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        return transfer(result);
    }


    @Override
    public Set<T> reverseRangeByScore(double min, double max) {
        Set<String> result = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        return transfer(result);
    }

    @Override
    public Long rank(T t) {
        return redisTemplate.opsForZSet().rank(key, JsonUtil.toJson(t));
    }

    @Override
    public Long reverseRank(T t) {
        return redisTemplate.opsForZSet().reverseRank(key, JsonUtil.toJson(t));
    }

    @Override
    public Long removeRangeByScore(double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    @Override
    public Long size() {
        return redisTemplate.opsForZSet().size(key);
    }

    private Set<T> transfer(Set<String> values) {
        if (null == values || values.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<T> res = new LinkedHashSet<>();
        for (String val : values) {
            T t = JsonUtil.fromJson(val, model);
            res.add(t);
        }
        return res;
    }
}
