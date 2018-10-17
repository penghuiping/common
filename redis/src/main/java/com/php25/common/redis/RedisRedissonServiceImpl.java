package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * redis服务 redisson实现
 *
 * @author penghuiping
 * @date 2016-09-2
 */
public class RedisRedissonServiceImpl implements RedisService {
    private static Logger logger = LoggerFactory.getLogger(RedisRedissonServiceImpl.class);

    private RedissonClient redisson;

    public RedisRedissonServiceImpl(RedissonClient redisson) {
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public <T> T get(final String key, Class<T> cls) {
        String value = (String) redisson.getBucket(key).get();
        try {
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
        String value = (String) redisson.getBucket(key).get();
        try {
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
            RBucket rBucket = redisson.getBucket(key);
            rBucket.set(JsonUtil.toJson(value));
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
            RBucket rBucket = redisson.getBucket(key);
            result = rBucket.trySet(JsonUtil.toJson(value));
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
            RBucket rBucket = redisson.getBucket(key);
            rBucket.set(JsonUtil.toJson(value), expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
        return result;
    }

    @Override
    public Long remainTimeToLive(String key) {
        RBucket rBucket = redisson.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    @Override
    public Long incr(String key) {
        return (redisson.getAtomicLong(key).getAndIncrement() + 1);
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        RBucket rBucket = redisson.getBucket(key);
        return rBucket.expire(expireTime, timeUnit);
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        RBucket rBucket = redisson.getBucket(key);
        return rBucket.expireAt(date);
    }

    @Override
    public RedisLockInfo tryLock(String redisKey, long expire, long tryTimeout) {
        RLock rLock = redisson.getLock(redisKey);
        try {
            Boolean result = rLock.tryLock(tryTimeout, expire, TimeUnit.MILLISECONDS);
            if (result != null && result == true) {
                String lockId = UUID.randomUUID().toString();
                RedisLockInfo redisLockInfo = new RedisLockInfo(lockId, redisKey, expire, tryTimeout, 1);
                return redisLockInfo;
            }
        } catch (InterruptedException e) {
            logger.error("尝试获取redis锁出错", e);
        }
        return null;
    }

    @Override
    public boolean releaseLock(RedisLockInfo redisLockInfo) {
        RLock rLock = redisson.getLock(redisLockInfo.getRedisKey());
        rLock.unlock();
        return true;
    }
}
