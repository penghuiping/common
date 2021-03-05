package com.php25.common.redis.local;

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
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Date;
import java.util.Optional;
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
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_SET_BIT);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.STRING_GET_BIT);

        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.REMOVE);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.EXISTS);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.GET_EXPIRE);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.EXPIRE);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.BLOOM_FILTER_GET);
        this.redisCmdDispatcher.registerHandler0(RedisStringHandlers.RATE_LIMIT_GET);

        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_ADD);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_REMOVE);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_MEMBERS);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_IS_MEMBER);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_POP);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_UNION);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_INTER);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_DIFF);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_SIZE);
        this.redisCmdDispatcher.registerHandler0(RedisSetHandlers.SET_GET_RANDOM_MEMBER);

        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_INIT);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_RIGHT_PUSH);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_LEFT_PUSH);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_RIGHT_POP);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_LEFT_POP);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_LEFT_RANGE);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_LEFT_TRIM);
        this.redisCmdDispatcher.registerHandler0(RedisListHandlers.LIST_SIZE);

        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_PUT);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_PUT_NX);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_HAS_KEY);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_GET);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_DELETE);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_INCR);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_DECR);
        this.redisCmdDispatcher.registerHandler0(RedisHashHandlers.HASH_DECR);
    }

    @Override
    public void remove(String... keys) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.REMOVE, Lists.newArrayList(keys));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.REMOVE, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public Boolean exists(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.EXISTS, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> result = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (result.isPresent()) {
            return (Boolean) result.get();
        }
        return false;
    }

    @Override
    public Long getExpire(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.GET_EXPIRE, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> result = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (result.isPresent()) {
            return (Long) result.get();
        }
        return null;
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.EXPIRE, Lists.newArrayList(key, expireTime, timeUnit));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> result = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (result.isPresent()) {
            return (Boolean) result.get();
        }
        return false;
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        return this.expire(key, date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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
    public RRateLimiter rateLimiter(int rate, String id) {
        return new LocalRateLimiter(id, rate, this);
    }
}
