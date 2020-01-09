package com.php25.common.redis;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/1/5 11:54
 */
public class RListImpl<T> implements RList<T> {

    private StringRedisTemplate redisTemplate;

    private String listKey;

    private Class<T> model;

    public RListImpl(StringRedisTemplate redisTemplate, String listKey, Class<T> cls) {
        this.redisTemplate = redisTemplate;
        this.listKey = listKey;
        this.model = cls;
    }

    @Override
    public Long rightPush(T value) {
        return redisTemplate.opsForList().rightPush(listKey, JsonUtil.toJson(value));
    }

    @Override
    public Long leftPush(T value) {
        return redisTemplate.opsForList().leftPush(listKey, JsonUtil.toJson(value));
    }

    @Override
    public T rightPop() {
        String json = redisTemplate.opsForList().rightPop(listKey);
        if(StringUtil.isBlank(json)) {
            return null;
        }else {
            return JsonUtil.fromJson(json, model);
        }
    }

    @Override
    public T leftPop() {
        String json = redisTemplate.opsForList().leftPop(listKey);
        if(StringUtil.isBlank(json)) {
            return null;
        }else {
            return JsonUtil.fromJson(json, model);
        }
    }

    @Override
    public List<T> leftRange(long start, long end) {
        List<String> list = redisTemplate.opsForList().range(listKey, start, end);
        if (null != list && !list.isEmpty()) {
            return list.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public void leftTrim(long start, long end) {
        redisTemplate.opsForList().trim(listKey, start, end);
    }

    @Override
    public Long size() {
        return redisTemplate.opsForList().size(listKey);
    }

    @Override
    public T blockLeftPop(long timeout, TimeUnit timeUnit) {
        String result = redisTemplate.opsForList().leftPop(listKey, timeout, timeUnit);
        if(StringUtil.isBlank(result)) {
            return null;
        }else {
            return JsonUtil.fromJson(result, model);
        }
    }

    @Override
    public T blockRightPop(long timeout, TimeUnit timeUnit) {
        String result = redisTemplate.opsForList().rightPop(listKey, timeout, timeUnit);
        if(StringUtil.isBlank(result)) {
            return null;
        }else {
            return JsonUtil.fromJson(result, model);
        }
    }
}
