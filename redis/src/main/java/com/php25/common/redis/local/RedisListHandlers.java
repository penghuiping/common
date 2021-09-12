package com.php25.common.redis.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

/**
 * @author penghuiping
 * @date 2021/3/3 11:06
 */
public class RedisListHandlers {
    static final Pair<String, RedisCmdHandler> LIST_INIT = Pair.of(RedisCmd.LIST_INIT, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        cache.putValue(key, expiredCache);
        response.setResult(expiredCache.getValue());
    });
    static final Pair<String, RedisCmdHandler> LIST_RIGHT_PUSH = Pair.of(RedisCmd.LIST_RIGHT_PUSH, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        list.addLast(element);
        flush(cache, key, list);
        response.setResult(list.size());
        list.getPipe().offer(true);
    });
    static final Pair<String, RedisCmdHandler> LIST_LEFT_PUSH = Pair.of(RedisCmd.LIST_LEFT_PUSH, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Object element = request.getParams().get(1);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        list.addFirst(element);
        flush(cache, key, list);
        response.setResult(list.size());
        list.getPipe().offer(true);
    });
    static final Pair<String, RedisCmdHandler> LIST_RIGHT_POP = Pair.of(RedisCmd.LIST_RIGHT_POP, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        if (list.isEmpty()) {
            response.setResult(null);
            return;
        }
        Object res = list.removeLast();
        flush(cache, key, list);
        response.setResult(res);
    });
    static final Pair<String, RedisCmdHandler> LIST_LEFT_POP = Pair.of(RedisCmd.LIST_LEFT_POP, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        if (list.isEmpty()) {
            response.setResult(null);
            return;
        }
        Object res = list.removeFirst();
        flush(cache, key, list);
        response.setResult(res);
    });
    static final Pair<String, RedisCmdHandler> LIST_LEFT_RANGE = Pair.of(RedisCmd.LIST_LEFT_RANGE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        int start = Integer.parseInt(request.getParams().get(1).toString());
        int end = Integer.parseInt(request.getParams().get(2).toString());
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        response.setResult(list.subList(start, end));
    });
    static final Pair<String, RedisCmdHandler> LIST_LEFT_TRIM = Pair.of(RedisCmd.LIST_LEFT_TRIM, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        int start = Integer.parseInt(request.getParams().get(1).toString());
        int end = Integer.parseInt(request.getParams().get(2).toString());
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        flush(cache, key, new LinkedListPlus<>(list.subList(start, end)));
        response.setResult(null);
    });
    static final Pair<String, RedisCmdHandler> LIST_SIZE = Pair.of(RedisCmd.LIST_SIZE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        LinkedListPlus<Object> list = (LinkedListPlus<Object>) expiredCache.getValue();
        response.setResult(list.size());
    });
    private static final Logger log = LoggerFactory.getLogger(RedisListHandlers.class);

    private static ExpiredCache getCacheValue(LruCachePlus cache, String key) {
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            LinkedListPlus<Object> linkedListPlus = new LinkedListPlus<>();
            expiredCache = new ExpiredCache(Constants.UNEXPIRED, key, linkedListPlus);
        }
        return expiredCache;
    }

    private static void flush(LruCachePlus cache, String key, Object value) {
        ExpiredCache expiredCache = getCacheValue(cache, key);
        expiredCache.setValue(value);
        cache.putValue(key, expiredCache);
    }
}
