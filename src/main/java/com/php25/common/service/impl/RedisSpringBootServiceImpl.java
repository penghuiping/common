package com.php25.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by penghuiping on 16/9/2.
 */
public class RedisSpringBootServiceImpl implements RedisService {
    private static Logger logger = LoggerFactory.getLogger(RedisSpringBootServiceImpl.class);

    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public RedisSpringBootServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        redisTemplate.delete(Lists.newArrayList(keys));
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, Class<T> cls) {
        try {
            String value = redisTemplate.boundValueOps(key).get();
            return objectMapper.readValue(value, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T get(final String key, TypeReference<T> cls) {
        try {
            String value = redisTemplate.boundValueOps(key).get();
            return objectMapper.readValue(value, cls);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.boundValueOps(key).set(objectMapper.writeValueAsString(value));
            result = true;
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 单位秒
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            redisTemplate.boundValueOps(key).set(objectMapper.writeValueAsString(value), expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
        return result;
    }

    @Override
    public Long remainTimeToLive(String key) {
        if (exists(key)) {
            return redisTemplate.boundValueOps(key).getExpire();
        } else {
            return -1l;
        }
    }

    @Override
    public Long incr(String key) {
        Long result = redisTemplate.execute((RedisCallback<Long>) redisConnection -> {
            return redisConnection.incr(key.getBytes());
        });
        return result;
    }
}
