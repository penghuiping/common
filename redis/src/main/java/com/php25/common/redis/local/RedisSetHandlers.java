package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author penghuiping
 * @date 2021/3/3 10:58
 */
class RedisSetHandlers {

    static final Pair<String, RedisCmdHandler> SET_ADD = Pair.of(RedisCmd.SET_ADD, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        set.add(element);
        flush(cache, key, set);
        response.setResult(null);
    });
    static final Pair<String, RedisCmdHandler> SET_REMOVE = Pair.of(RedisCmd.SET_REMOVE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        set.remove(element);
        flush(cache, key, set);
        response.setResult(null);
    });
    static final Pair<String, RedisCmdHandler> SET_MEMBERS = Pair.of(RedisCmd.SET_MEMBERS, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        response.setResult(set);
    });
    static final Pair<String, RedisCmdHandler> SET_IS_MEMBER = Pair.of(RedisCmd.SET_IS_MEMBER, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        response.setResult(set.contains(element));
    });
    static final Pair<String, RedisCmdHandler> SET_POP = Pair.of(RedisCmd.SET_POP, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();

        Iterator iterator = set.iterator();
        if (iterator.hasNext()) {
            Object val = iterator.next();
            set.remove(val);
            flush(cache, key, set);
            response.setResult(val);
            return;
        }
        response.setResult(null);
    });
    static final Pair<String, RedisCmdHandler> SET_UNION = Pair.of(RedisCmd.SET_UNION, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String otherSetKey = request.getParams().get(1).toString();

        ExpiredCache expiredCache = getCacheValue(cache, key);
        ExpiredCache expiredCache1 = getCacheValue(cache, otherSetKey);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        Set<Object> set1 = (Set<Object>) expiredCache1.getValue();

        Set<Object> newSet = new HashSet<>(set);
        newSet.addAll(set1);
        response.setResult(newSet);
    });
    static final Pair<String, RedisCmdHandler> SET_INTER = Pair.of(RedisCmd.SET_INTER, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String otherSetKey = request.getParams().get(1).toString();

        ExpiredCache expiredCache = getCacheValue(cache, key);
        ExpiredCache expiredCache1 = getCacheValue(cache, otherSetKey);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        Set<Object> set1 = (Set<Object>) expiredCache1.getValue();

        Set<Object> newSet = new HashSet<>(set);
        newSet.retainAll(set1);
        response.setResult(newSet);

    });
    static final Pair<String, RedisCmdHandler> SET_DIFF = Pair.of(RedisCmd.SET_DIFF, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        String otherSetKey = request.getParams().get(1).toString();

        ExpiredCache expiredCache = getCacheValue(cache, key);
        ExpiredCache expiredCache1 = getCacheValue(cache, otherSetKey);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        Set<Object> set1 = (Set<Object>) expiredCache1.getValue();
        Set<Object> newSet = new HashSet<>(set);
        newSet.removeAll(set1);
        response.setResult(newSet);
    });
    static final Pair<String, RedisCmdHandler> SET_SIZE = Pair.of(RedisCmd.SET_SIZE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();
        response.setResult(set.size());
    });
    static final Pair<String, RedisCmdHandler> SET_GET_RANDOM_MEMBER = Pair.of(RedisCmd.SET_GET_RANDOM_MEMBER, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        Set<Object> set = (Set<Object>) expiredCache.getValue();

        Iterator iterator = set.iterator();
        if (iterator.hasNext()) {
            Object val = iterator.next();
            response.setResult(val);
            return;
        }
        response.setResult(null);
    });

    private static ExpiredCache getCacheValue(LruCachePlus cache, String key) {
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.UNEXPIRED, key, new HashSet<>());
        }
        return expiredCache;
    }

    private static void flush(LruCachePlus cache, String key, Object value) {
        ExpiredCache expiredCache = getCacheValue(cache, key);
        expiredCache.setValue(value);
        cache.putValue(key, expiredCache);
    }
}
