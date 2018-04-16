package com.php25.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.service.RedisService;
import org.apache.log4j.Logger;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * Created by penghuiping on 16/9/2.
 */
public class RedisServiceImpl implements RedisService {
    private RedissonClient redisson;
    @Autowired
    private ObjectMapper objectMapper;

    public RedisServiceImpl(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public RedissonClient getRedission() {
        return this.redisson;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            redisson.getBucket(key).delete();
        }
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            RBucket rBucket = redisson.getBucket(key);
            rBucket.delete();
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        RBucket rBucket = redisson.getBucket(key);
        return (null != rBucket.get());
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, Class<T> cls) {
        String value = (String) redisson.getBucket(key).get();
        try {
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
        String value = (String) redisson.getBucket(key).get();
        try {
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
            RBucket rBucket = redisson.getBucket(key);
            rBucket.set(objectMapper.writeValueAsString(value));
            result = true;
        } catch (Exception e) {
            Logger.getLogger(RedisServiceImpl.class).error(e);
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
            RBucket rBucket = redisson.getBucket(key);
            rBucket.set(objectMapper.writeValueAsString(value), expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            Logger.getLogger(RedisServiceImpl.class).error(e);
        }
        return result;
    }

    @Override
    public Long remainTimeToLive(String key) {
        RBucket rBucket = redisson.getBucket(key);
        return rBucket.remainTimeToLive();
    }
}
