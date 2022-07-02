package com.php25.common.redis.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.redis.RString;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/1/4 17:36
 */
public class RStringImpl implements RString {

    private static final String LUA_SCRIPT_0 = "local flag = redis.call('SETNX', KEYS[1],tonumber(ARGV[1]))\n" +
            "redis.call('PEXPIRE', KEYS[1], tonumber(ARGV[2]))\n" +
            "return redis.call('INCR', KEYS[1])\n";

    private final StringRedisTemplate stringRedisTemplate;

    public RStringImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public <T> T get(String key, Class<T> cls) {
        String value = get(key);
        if (StringUtil.isBlank(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, cls);
        }
    }

    @Override
    public <T> T get(String key, TypeReference<T> cls) {
        String value = get(key);
        if (StringUtil.isBlank(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, cls);
        }
    }

    private String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    private Boolean set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
        return true;
    }

    @Override
    public Boolean set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JsonUtil.toJson(value));
        return true;
    }

    @Override
    public Boolean set(String key, Object value, Long expireTime) {
        stringRedisTemplate.opsForValue().set(key, JsonUtil.toJson(value), expireTime, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Boolean setNx(String key, Object value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, JsonUtil.toJson(value));
    }

    @Override
    public Boolean setNx(String key, Object value, Long expireTime) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, JsonUtil.toJson(value), expireTime, TimeUnit.SECONDS);
    }

    @Override
    public Long incr(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    @Override
    public Boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    @Override
    public Long incrWithInitialValueAndSetExpiration(String key, Long value, Long expiration) {
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>(LUA_SCRIPT_0, Long.class);
        return stringRedisTemplate.execute(defaultRedisScript, Lists.newArrayList(key), String.valueOf(value), String.valueOf(expiration));
    }

}
