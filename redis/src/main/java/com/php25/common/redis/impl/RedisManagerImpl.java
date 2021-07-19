package com.php25.common.redis.impl;

import com.google.common.collect.Lists;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RHash;
import com.php25.common.redis.RHyperLogLogs;
import com.php25.common.redis.RList;
import com.php25.common.redis.RRateLimiter;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RSortedSet;
import com.php25.common.redis.RString;
import com.php25.common.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis服务spring redis实现
 *
 * @author penghuiping
 * @date 2016-09-02
 */
public class RedisManagerImpl implements RedisManager {
    private static final Logger logger = LoggerFactory.getLogger(RedisManagerImpl.class);
    private final StringRedisTemplate redisTemplate;

    private final LockRegistry lockRegistry;

    private final RString rString;

    private static final String DEFAULT_REDIS_LOCK_NAME = "lock:spring_redis";


    public RedisManagerImpl(StringRedisTemplate redisTemplate) {
        this(redisTemplate, DEFAULT_REDIS_LOCK_NAME);
    }

    public RedisManagerImpl(StringRedisTemplate redisTemplate, String redisLockKey) {
        this.redisTemplate = redisTemplate;
        this.lockRegistry = new RedisLockRegistry(Objects.requireNonNull(redisTemplate.getConnectionFactory()), redisLockKey);
        this.rString = new RStringImpl(redisTemplate);
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void remove(final String... keys) {
        redisTemplate.delete(Lists.newArrayList(keys));
    }

    @Override
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public Boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
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
    public Lock lock(String lockKey) {
        return lockRegistry.obtain(lockKey);
    }

    @Override
    public RString string() {
        return this.rString;
    }

    @Override
    public <T> RHash<T> hash(String hashKey, Class<T> cls) {
        return new RHashImpl<>(redisTemplate, hashKey, cls);
    }

    @Override
    public <T> RList<T> list(String listKey, Class<T> cls) {
        return new RListImpl<>(redisTemplate, listKey, cls);
    }

    @Override
    public <T> RSet<T> set(String setKey, Class<T> cls) {
        return new RSetImpl<>(redisTemplate, setKey, cls);
    }

    @Override
    public RBloomFilter bloomFilter(String name, long expectedInsertions, double fpp) {
        return new RBloomFilterImpl(redisTemplate, name, expectedInsertions, fpp);
    }

    @Override
    public RHyperLogLogs hyperLogLogs(String key) {
        return new RHyperLogLogsImpl(redisTemplate, key);
    }

    @Override
    public <T> RSortedSet<T> zset(String setKey, Class<T> cls) {
        return new RSortedSetImpl<>(redisTemplate, setKey, cls);
    }

    @Override
    public RRateLimiter rateLimiter(int rate, String id) {
        return new RRateLimiterImpl(redisTemplate, rate, rate, id);
    }
}
