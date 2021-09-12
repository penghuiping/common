package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

import java.util.HashMap;

/**
 * @author penghuiping
 * @date 2021/3/3 11:07
 */
public class RedisHashHandlers {

    static final Pair<String, RedisCmdHandler> HASH_PUT = Pair.of(RedisCmd.HASH_PUT, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        Object hashValue = request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        map.put(hashKey, hashValue);
        flush(cache, key, map);
        response.setResult(true);
    });

    static final Pair<String, RedisCmdHandler> HASH_GET = Pair.of(RedisCmd.HASH_GET, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        response.setResult(map.get(hashKey));
    });

    static final Pair<String, RedisCmdHandler> HASH_DELETE = Pair.of(RedisCmd.HASH_DELETE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        map.remove(hashKey);
        flush(cache, key, map);
        response.setResult(null);
    });

    static final Pair<String, RedisCmdHandler> HASH_INCR = Pair.of(RedisCmd.HASH_INCR, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();

        Object val = map.get(hashKey);
        if (null == val) {
            val = 1L;
            map.put(hashKey, val);
            flush(cache, key, map);
            response.setResult(val);
            return;
        }
        Long value = (Long) val;
        value = value + 1;
        map.put(hashKey, value);
        flush(cache, key, map);
        response.setResult(value);
    });

    static final Pair<String, RedisCmdHandler> HASH_DECR = Pair.of(RedisCmd.HASH_DECR, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        Object val = map.get(hashKey);
        if (null == val) {
            val = 1L;
            map.put(hashKey, val);
            flush(cache, key, map);
            response.setResult(val);
            return;
        }
        Long value = (Long) val;
        value = value - 1;
        map.put(hashKey, value);
        flush(cache, key, map);
        response.setResult(value);
    });

    static final Pair<String, RedisCmdHandler> HASH_PUT_NX = Pair.of(RedisCmd.HASH_PUT_NX, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        Object hashValue = request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        map.putIfAbsent(hashKey, hashValue);
        flush(cache, key, map);
        response.setResult(true);
    });

    static final Pair<String, RedisCmdHandler> HASH_HAS_KEY = Pair.of(RedisCmd.HASH_HAS_KEY, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String hashKey = request.getParams().get(1).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        HashMap<String, Object> map = (HashMap<String, Object>) expiredCache.getValue();
        response.setResult(map.containsKey(hashKey));
    });

    private static ExpiredCache getCacheValue(LruCachePlus cache, String key) {
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.UNEXPIRED, key, new HashMap<String, Object>());
        }
        return expiredCache;
    }

    private static void flush(LruCachePlus cache, String key, Object value) {
        ExpiredCache expiredCache = getCacheValue(cache, key);
        expiredCache.setValue(value);
        cache.putValue(key, expiredCache);
    }
}
