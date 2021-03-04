package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

import java.util.LinkedList;

/**
 * @author penghuiping
 * @date 2021/3/3 11:06
 */
public class RedisListHandlers {

    static final Pair<String, RedisCmdHandler> LIST_RIGHT_PUSH = Pair.of(RedisCmd.LIST_RIGHT_PUSH, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        list.addLast(element);
        flush(cache, key, list);
        response.setResult(list.size());
    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_PUSH = Pair.of(RedisCmd.LIST_LEFT_PUSH, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        list.addFirst(element);
        flush(cache, key, list);
        response.setResult(list.size());
    });

    static final Pair<String, RedisCmdHandler> LIST_RIGHT_POP = Pair.of(RedisCmd.LIST_RIGHT_POP, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        Object res = list.removeLast();
        flush(cache, key, list);
        response.setResult(res);
    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_POP = Pair.of(RedisCmd.LIST_LEFT_POP, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        Object res = list.removeFirst();
        flush(cache, key, list);
        response.setResult(res);
    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_RANGE = Pair.of(RedisCmd.LIST_LEFT_RANGE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        int start = (int) request.getParams().get(1);
        int end = (int) request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        response.setResult(list.subList(start, end));
    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_TRIM = Pair.of(RedisCmd.LIST_LEFT_TRIM, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        int start = (int) request.getParams().get(1);
        int end = (int) request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        flush(cache, key, list.subList(start, end));
    });


    static final Pair<String, RedisCmdHandler> LIST_SIZE = Pair.of(RedisCmd.LIST_SIZE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedList<Object> list = (LinkedList<Object>) expiredCache.getValue();
        response.setResult(list.size());
    });


    static final Pair<String, RedisCmdHandler> LIST_BLOCK_LEFT_POP = Pair.of(RedisCmd.LIST_BLOCK_LEFT_POP, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_BLOCK_RIGHT_POP = Pair.of(RedisCmd.LIST_BLOCK_RIGHT_POP, (redisManager, request, response) -> {

    });

    private static ExpiredCache getCacheValue(LruCachePlus cache, String key) {
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, key, new LinkedList<>());
        }
        return expiredCache;
    }

    private static void flush(LruCachePlus cache, String key, Object value) {
        ExpiredCache expiredCache = getCacheValue(cache, key);
        expiredCache.setValue(value);
        cache.putValue(key, expiredCache);
    }
}
