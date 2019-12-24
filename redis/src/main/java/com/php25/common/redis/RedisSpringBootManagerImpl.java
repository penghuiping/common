package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis服务spring redis实现
 *
 * @author penghuiping
 * @date 2016-09-02
 */
public class RedisSpringBootManagerImpl implements RedisManager {
    private static Logger logger = LoggerFactory.getLogger(RedisSpringBootManagerImpl.class);
    private StringRedisTemplate redisTemplate;

    private LockRegistry lockRegistry;

    public RedisSpringBootManagerImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.lockRegistry = new RedisLockRegistry(redisTemplate.getConnectionFactory(), "RedisSpringBootService_lock");
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    @Override
    public void remove(final String... keys) {
        redisTemplate.delete(Lists.newArrayList(keys));
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    @Override
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
    @Override
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    @Override
    public <T> T get(final String key, Class<T> cls) {
        try {
            String value = redisTemplate.boundValueOps(key).get();
            return JsonUtil.fromJson(value, cls);
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
    @Override
    public <T> T get(final String key, TypeReference<T> cls) {
        try {
            String value = redisTemplate.boundValueOps(key).get();
            return JsonUtil.fromJson(value, cls);
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
    @Override
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.boundValueOps(key).set(JsonUtil.toJson(value));
            result = true;
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
        return result;
    }

    @Override
    public boolean setNx(String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.boundValueOps(key).setIfAbsent(JsonUtil.toJson(value));
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
    @Override
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            redisTemplate.boundValueOps(key).set(JsonUtil.toJson(value), expireTime, TimeUnit.SECONDS);
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
            return -1L;
        }
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.execute((RedisCallback<Long>) redisConnection -> redisConnection.incr(key.getBytes()));
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        return redisTemplate.expire(key, expireTime, timeUnit);
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    @Override
    public Lock obtainDistributeLock(String lockKey) {
        return lockRegistry.obtain(lockKey);
    }

    @Override
    public Boolean setIntoMap(String mapKey, String key, Object value) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(mapKey);
        boundHashOperations.put(key, JsonUtil.toJson(value));
        return true;
    }

    @Override
    public <T> T getFromMap(String mapKey, String key, Class<T> cls) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(mapKey);
        return JsonUtil.fromJson(boundHashOperations.get(key), cls);
    }

    @Override
    public <T> T getFromMap(String mapKey, String key, TypeReference<T> cls) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(mapKey);
        return JsonUtil.fromJson(boundHashOperations.get(key), cls);
    }
}
