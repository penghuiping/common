package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.TimeUtil;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RHash;
import com.php25.common.redis.RHyperLogLogs;
import com.php25.common.redis.RList;
import com.php25.common.redis.RRateLimiter;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RSortedSet;
import com.php25.common.redis.RString;
import com.php25.common.redis.RedisManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class LocalRedisManager implements RedisManager, DisposableBean {
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
    }

    void cleanAllExpiredKeys() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.CLEAN_ALL_EXPIRE, null);
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        this.redisCmdDispatcher.stop();
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
        return result.map(o -> (Boolean) o).orElse(false);
    }

    @Override
    public Long getExpire(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.GET_EXPIRE, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> result = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        return (Long) result.orElse(null);
    }

    @Override
    public Boolean expire(String key, Long expireTime, TimeUnit timeUnit) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.EXPIRE, Lists.newArrayList(key, expireTime, timeUnit));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> result = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        return result.map(o -> (Boolean) o).orElse(false);
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
        return new LocalSortedSet<>(setKey, cls, this);
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

    @Override
    public String getDatetimeSequenceNumber(String prefix) {
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long incr = this.string().incr(String.format("%s:%s", prefix, todayStr));
        if (incr < 2) {
            this.expireAt(String.format("%s:%s", prefix, todayStr), TimeUtil.getEndTimeOfDay(new Date()));
        }

        if (incr > 999999999999L) {
            throw Exceptions.throwIllegalStateException("已到达序列最大值无法继续生产新的序列");
        }

        //%d,代表整数类型(十进制),%012d表示输出宽度为12，不足12位的补0
        return todayStr + String.format("%012d", incr);
    }
}
