package com.php25.common.redis;

import com.php25.common.core.util.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/1/9 13:12
 */
public class RSortedSetImpl<T> implements RSortedSet<T> {

    private StringRedisTemplate redisTemplate;

    private String key;

    private Class<T> model;

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
        Set<T> resultSet = null;
        if (null != result && !result.isEmpty()) {
            resultSet = result.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toSet());
        }
        return resultSet;
    }

    @Override
    public Set<T> reverseRange(long start, long end) {
        Set<String> result = redisTemplate.opsForZSet().reverseRange(key, start, end);
        Set<T> resultSet = null;
        if (null != result && !result.isEmpty()) {
            resultSet = result.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toSet());
        }
        return resultSet;
    }

    @Override
    public Set<T> rangeByScore(double min, double max) {
        Set<String> result = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        Set<T> resultSet = null;
        if (null != result && !result.isEmpty()) {
            resultSet = result.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toSet());
        }
        return resultSet;
    }

    @Override
    public Set<T> reverseRangeByScore(double min, double max) {
        Set<String> result = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        Set<T> resultSet = null;
        if (null != result && !result.isEmpty()) {
            resultSet = result.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toSet());
        }
        return resultSet;
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
        return  redisTemplate.opsForZSet().removeRangeByScore(key,min,max);
    }
}
