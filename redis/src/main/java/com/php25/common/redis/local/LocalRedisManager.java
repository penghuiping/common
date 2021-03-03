package com.php25.common.redis.local;

import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RHash;
import com.php25.common.redis.RHyperLogLogs;
import com.php25.common.redis.RList;
import com.php25.common.redis.RRateLimiter;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RSortedSet;
import com.php25.common.redis.RString;
import com.php25.common.redis.RedisManager;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 此类使用本地方法模拟redis的api，调用此类无需连上redis，就可以使用类似与redis的api能力
 * 实现jdk内置方法
 *
 * @author penghuiping
 * @date 2021/2/24 16:09
 */
public class LocalRedisManager implements RedisManager {
    LruCachePlus cache;

    RString rString;

    RedisCmdDispatcher redisCmdDispatcher;

    ConversionService conversionService;


    public LocalRedisManager(Integer maxEntry) {
        this.cache = new LruCachePlusLocal(maxEntry);
        this.rString = new LocalString(this);
        this.conversionService = DefaultConversionService.getSharedInstance();
        this.redisCmdDispatcher = new RedisCmdDispatcher();
        this.redisCmdDispatcher.setRedisManager(this);

        this.init();
    }

    private void init() {
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_GET);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_SET);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_SET_NX);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_INCR);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_DECR);
    }

    @Override
    public void remove(String... keys) {
        for (String key : keys) {
            this.cache.remove(key);
        }
    }

    @Override
    public void remove(String key) {
        this.cache.remove(key);
    }

    @Override
    public Boolean exists(String key) {
        boolean res = this.cache.containsKey(key);
        if (res && this.getExpire(key) > 0) {
            return true;
        } else {
            this.remove(key);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        return TimeUnit.of(ChronoUnit.MILLIS).toSeconds(this.cache.getValue(key).getExpiredTime() - Instant.now().toEpochMilli());
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        ExpiredCache expiredCacheObject = this.cache.getValue(key);
        if (null == expiredCacheObject) {
            return false;
        }
        expiredCacheObject.setExpiredTime(Instant.now().toEpochMilli() + timeUnit.toMillis(expireTime));
        return true;
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        ExpiredCache expiredCacheObject = this.cache.getValue(key);
        if (null == expiredCacheObject) {
            return false;
        }
        expiredCacheObject.setExpiredTime(date.getTime());
        return true;
    }

    @Override
    public Lock lock(String lockKey) {
        ExpiredCache expiredCache = this.cache.getValue(lockKey);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, lockKey, new ReentrantLock());
        }
        return (ReentrantLock) expiredCache.getValue();
    }

    @Override
    public RString string() {
        return this.rString;
    }

    @Override
    public <T> RHash<T> hash(String hashKey, Class<T> cls) {
        return new LocalHash<>(hashKey, cls, this);
    }

    @Override
    public <T> RList<T> list(String listKey, Class<T> cls) {
        return new LocalList<>(listKey, cls, this);
    }

    @Override
    public <T> RSet<T> set(String setKey, Class<T> cls) {
        return new LocalSet<>(setKey, cls, this);
    }

    @Override
    public <T> RSortedSet<T> zset(String setKey, Class<T> cls) {
        return null;
    }

    @Override
    public RBloomFilter bloomFilter(String name, long expectedInsertions, double fpp) {
        return new LocalBloomFilter(name, expectedInsertions, fpp, this);
    }

    @Override
    public RHyperLogLogs hyperLogLogs(String key) {
        return null;
    }

    @Override
    public RRateLimiter rateLimiter(int capacity, int rate, String id) {
        return new LocalRateLimiter(id, rate, this);
    }
}
