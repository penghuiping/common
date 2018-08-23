package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * redis服务spring redis实现
 * @author penghuiping
 * @date 2016-09-02
 */
public class RedisSpringBootServiceImpl implements RedisService {
    private static final String LUA_SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
    private static final RedisScript<String> SCRIPT_LOCK = new DefaultRedisScript<String>(LUA_SCRIPT_LOCK, String.class);
    private static final String LUA_SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])) else return '0' end";
    private static final RedisScript<String> SCRIPT_UNLOCK = new DefaultRedisScript<String>(LUA_SCRIPT_UNLOCK, String.class);
    private static Logger logger = LoggerFactory.getLogger(RedisSpringBootServiceImpl.class);
    private StringRedisTemplate redisTemplate;

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
        Long result = redisTemplate.execute((RedisCallback<Long>) redisConnection -> {
            return redisConnection.incr(key.getBytes());
        });
        return result;
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        return redisTemplate.expire(key, expireTime, timeUnit);
    }

    @Override
    public RedisLockInfo tryLock(String redisKey, long expire, long tryTimeout) {
        Assert.isTrue(tryTimeout > 0, "tryTimeout必须大于0");
        long timestamp = System.currentTimeMillis();
        int tryCount = 0;
        String lockId = UUID.randomUUID().toString();
        while ((System.currentTimeMillis() - timestamp) < tryTimeout) {
            try {
                Object lockResult = redisTemplate.execute(SCRIPT_LOCK,
                        redisTemplate.getStringSerializer(),
                        redisTemplate.getStringSerializer(),
                        Collections.singletonList(redisKey),
                        lockId, String.valueOf(expire));
                tryCount++;
                if (null != lockResult && "OK".equals(lockResult)) {
                    return new RedisLockInfo(lockId, redisKey, expire, tryTimeout, tryCount);
                } else {
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                logger.error("尝试获取redis锁出错", e);
            }
        }
        return null;
    }

    @Override
    public boolean releaseLock(RedisLockInfo redisLockInfo) {
        Object releaseResult = null;
        try {
            releaseResult = redisTemplate.execute(SCRIPT_UNLOCK,
                    redisTemplate.getStringSerializer(),
                    redisTemplate.getStringSerializer(),
                    Collections.singletonList(redisLockInfo.getRedisKey()),
                    redisLockInfo.getLockId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null != releaseResult && releaseResult.equals(1);
    }
}
