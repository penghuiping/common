package com.php25.common.redis.local;

import com.google.common.util.concurrent.RateLimiter;
import com.php25.common.core.mess.StringBloomFilter;
import com.php25.common.core.util.JsonUtil;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author penghuiping
 * @date 2021/3/3 09:15
 */
class RedisStringHandlers {

    final static Pair<String, RedisCmdHandler> STRING_GET = Pair.of(RedisCmd.STRING_GET, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCacheObject = cache.getValue(key);
        //没有此缓存
        if (expiredCacheObject == null) {
            response.setResult(null);
            return;
        }

        //缓存过期
        if (expiredCacheObject.isExpired()) {
            cache.remove(key);
            response.setResult(null);
            return;
        }
        response.setResult(expiredCacheObject);
    });

    final static Pair<String, RedisCmdHandler> STRING_SET = Pair.of(RedisCmd.STRING_SET, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object value = request.getParams().get(1);
        Long expireTime = Constants.DEFAULT_EXPIRED_TIME;
        if (request.getParams().size() > 2) {
            expireTime = (Long) request.getParams().get(2);
        }
        ExpiredCache expiredCache = new ExpiredCache(expireTime, key, JsonUtil.toJson(value));
        cache.putValue(key, expiredCache);
        response.setResult(expiredCache);
    });

    final static Pair<String, RedisCmdHandler> STRING_SET_NX = Pair.of(RedisCmd.STRING_SET_NX, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object value = request.getParams().get(1);
        Long expireTime = Constants.DEFAULT_EXPIRED_TIME;
        if (request.getParams().size() > 2) {
            expireTime = (Long) request.getParams().get(2);
        }
        ExpiredCache expiredCache = new ExpiredCache(expireTime, key, JsonUtil.toJson(value));
        cache.getValue(key);
        cache.putValueIfAbsent(key, expiredCache);
        cache.getValue(key);
        response.setResult(expiredCache);
    });

    final static Pair<String, RedisCmdHandler> STRING_INCR = Pair.of(RedisCmd.STRING_INCR, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            Long res = 1L;
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, JsonUtil.toJson(res));
            cache.putValue(key, expiredCache);
            response.setResult(expiredCache);
            return;
        }
        Long res = JsonUtil.fromJson(expiredCache.getValue().toString(), Long.class);
        res = res + 1;
        expiredCache.setValue(JsonUtil.toJson(res));
        cache.putValue(key, expiredCache);
        response.setResult(expiredCache);
    });

    final static Pair<String, RedisCmdHandler> STRING_DECR = Pair.of(RedisCmd.STRING_DECR, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            Long res = 1L;
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, JsonUtil.toJson(res));
            cache.putValue(key, expiredCache);
            response.setResult(expiredCache);
            return;
        }
        Long res = JsonUtil.fromJson(expiredCache.getValue().toString(), Long.class);
        res = res - 1;
        expiredCache.setValue(JsonUtil.toJson(res));
        cache.putValue(key, expiredCache);
        response.setResult(expiredCache);
    });

    final static Pair<String, RedisCmdHandler> REMOVE = Pair.of(RedisCmd.REMOVE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        List<Object> keys = request.getParams();
        for (Object key : keys) {
            cache.remove(key.toString());
        }
        response.setResult(true);
    });

    final static Pair<String, RedisCmdHandler> CLEAN_ALL_EXPIRE = Pair.of(RedisCmd.CLEAN_ALL_EXPIRE, (redisManager, request, response) -> {
        LinkedHashMap<String, ExpiredCache> cache = (LinkedHashMap<String, ExpiredCache>) redisManager.cache;
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        response.setResult(true);
    });

    final static Pair<String, RedisCmdHandler> EXISTS = Pair.of(RedisCmd.EXISTS, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        boolean res = cache.containsKey(key);
        response.setResult(res);

        boolean isExpire = (cache.getValue(key).getExpiredTime() - Instant.now().toEpochMilli()) <= 0;

        if (res && isExpire) {
            cache.remove(key);
            response.setResult(false);
            return;
        }
        response.setResult(true);
    });

    final static Pair<String, RedisCmdHandler> GET_EXPIRE = Pair.of(RedisCmd.GET_EXPIRE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Long expireTime = TimeUnit.MILLISECONDS.toSeconds(cache.getValue(key).getExpiredTime() - Instant.now().toEpochMilli());
        response.setResult(expireTime);
    });

    final static Pair<String, RedisCmdHandler> EXPIRE = Pair.of(RedisCmd.EXPIRE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Long expireTime = (Long) request.getParams().get(1);
        TimeUnit timeUnit = (TimeUnit) request.getParams().get(2);

        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            response.setResult(false);
            return;
        }
        expiredCache.setExpiredTime(Instant.now().toEpochMilli() + timeUnit.toMillis(expireTime));
        cache.putValue(key, expiredCache);
        response.setResult(true);
    });

    final static Pair<String, RedisCmdHandler> BLOOM_FILTER_GET = Pair.of(RedisCmd.BLOOM_FILTER_GET, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        int expectedInsertions = Integer.parseInt(request.getParams().get(1).toString());
        double fpp = Double.parseDouble(request.getParams().get(2).toString());
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, new StringBloomFilter(expectedInsertions, fpp));
            cache.putValue(key, expiredCache);
        }
        response.setResult(expiredCache.getValue());
    });

    final static Pair<String, RedisCmdHandler> RATE_LIMIT_GET = Pair.of(RedisCmd.RATE_LIMIT_GET, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        double rate = Double.parseDouble(request.getParams().get(1).toString());
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, RateLimiter.create(rate));
            cache.putValue(key, expiredCache);
        }
        response.setResult(expiredCache.getValue());
    });

    final static Pair<String, RedisCmdHandler> STRING_SET_BIT = Pair.of(RedisCmd.STRING_SET_BIT, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        long offset = Long.parseLong(request.getParams().get(1).toString());
        boolean value = Boolean.parseBoolean(request.getParams().get(2).toString());
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, 0L);
            cache.putValue(key, expiredCache);
        }
        long val0 = Long.parseLong(expiredCache.getValue().toString());
        if (value) {
            //设置1
            long val = 1L << offset;
            val0 = val0 | val;

        } else {
            //设置0
            long val = ~(1L << offset);
            val0 = val0 & val;
        }
        expiredCache.setValue(val0);
        cache.putValue(key, expiredCache);
        response.setResult(true);
    });

    final static Pair<String, RedisCmdHandler> STRING_GET_BIT = Pair.of(RedisCmd.STRING_GET_BIT, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        long offset = Long.parseLong(request.getParams().get(1).toString());
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, 0L);
            cache.putValue(key, expiredCache);
        }
        Long val0 = Long.parseLong(expiredCache.getValue().toString());
        long val = 1L << offset;
        val0 = val0 & val;
        val0 = val0 >> offset;
        response.setResult(val0 == 1);
    });
}
