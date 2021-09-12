package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;
import com.php25.common.core.util.JsonUtil;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author penghuiping
 * @date 2021/3/14 22:52
 */
public class RedisSortedSetHandlers {

    static final Pair<String, RedisCmdHandler> SORTED_SET_ADD = Pair.of(RedisCmd.SORTED_SET_ADD, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Double score = (Double) request.getParams().get(1);
        Object element = request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> map = (TreeMultimap<Double, String>) expiredCache.getValue();
        map.put(score, JsonUtil.toJson(element));
        flush(cache, key, map);
        response.setResult(null);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_SIZE = Pair.of(RedisCmd.SORTED_SET_SIZE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> map = (TreeMultimap<Double, String>) expiredCache.getValue();
        response.setResult(map.size());
    });


    static final Pair<String, RedisCmdHandler> SORTED_SET_RANGE = Pair.of(RedisCmd.SORTED_SET_RANGE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Long start = (Long) request.getParams().get(2);
        Long end = (Long) request.getParams().get(3);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> map = (TreeMultimap<Double, String>) expiredCache.getValue();
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.asMap().entrySet().iterator();
        int count = 0;
        Set<Object> res = new LinkedHashSet<>();
        while (iterator.hasNext() && count <= end) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            Collection<String> values = entry.getValue();
            for (String value : values) {
                if (count >= start && count <= end) {
                    res.add(JsonUtil.fromJson(value, cls));
                    count++;
                }
            }
        }
        response.setResult(res);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANGE = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANGE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Long start = (Long) request.getParams().get(2);
        Long end = (Long) request.getParams().get(3);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> map = (TreeMultimap<Double, String>) expiredCache.getValue();

        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.asMap().descendingMap().entrySet().iterator();
        int count = 0;
        Set<Object> res = new LinkedHashSet<>();
        while (iterator.hasNext() && count <= end) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (int i = values.size() - 1; i >= 0; i--) {
                String value = values.get(i);
                if (count >= start && count <= end) {
                    res.add(JsonUtil.fromJson(value, cls));
                    count++;
                }
            }
        }
        response.setResult(res);
    });


    static final Pair<String, RedisCmdHandler> SORTED_SET_RANK = Pair.of(RedisCmd.SORTED_SET_RANK, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Object element = request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> treeMap = (TreeMultimap<Double, String>) expiredCache.getValue();

        Iterator<Map.Entry<Double, Collection<String>>> iterator = treeMap.asMap().entrySet().iterator();
        long count = 0;
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (String value : values) {
                Object val = JsonUtil.fromJson(value, cls);
                if (element.equals(val)) {
                    response.setResult(count);
                    return;
                }
                ++count;
            }
        }
        response.setResult(count);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANK = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANK, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Object element = request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> treeMap = (TreeMultimap<Double, String>) expiredCache.getValue();

        Iterator<Map.Entry<Double, Collection<String>>> iterator = treeMap.asMap().entrySet().iterator();
        long count = 0;
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (String value : values) {
                Object val = JsonUtil.fromJson(value, cls);
                if (element.equals(val)) {
                    response.setResult(treeMap.size() - 1 - count);
                    return;
                }
                ++count;
            }
        }

        response.setResult(treeMap.size() - 1 - count);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_RANGE_BY_SCORE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Double min = (Double) request.getParams().get(2);
        Double max = (Double) request.getParams().get(3);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> treeMap = (TreeMultimap<Double, String>) expiredCache.getValue();

        SortedMap<Double, Collection<String>> map = treeMap.asMap().subMap(min, true, max, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        Set<Object> res = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            Collection<String> values = entry.getValue();
            for (String value : values) {
                res.add(JsonUtil.fromJson(value, cls));
            }
        }
        response.setResult(res);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANGE_BY_SCORE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Class<?> cls = (Class<?>) request.getParams().get(1);
        Double min = (Double) request.getParams().get(2);
        Double max = (Double) request.getParams().get(3);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> treeMap = (TreeMultimap<Double, String>) expiredCache.getValue();

        SortedMap<Double, Collection<String>> map = treeMap.asMap().descendingMap().subMap(max, true, min, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        Set<Object> res = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (int i = values.size() - 1; i >= 0; i--) {
                String value = values.get(i);
                res.add(JsonUtil.fromJson(value, cls));
            }
        }
        response.setResult(res);
    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REMOVE_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_REMOVE_RANGE_BY_SCORE, (redisManager, request, response) -> {
        LruCachePlus cache = redisManager.cache;
        String key = request.getParams().get(0).toString();
        Double min = (Double) request.getParams().get(1);
        Double max = (Double) request.getParams().get(2);
        ExpiredCache expiredCache = getCacheValue(cache, key);
        TreeMultimap<Double, String> treeMap = (TreeMultimap<Double, String>) expiredCache.getValue();

        SortedMap<Double, Collection<String>> map = treeMap.asMap().subMap(min, true, max, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        long count = 0L;
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            iterator.remove();
            Collection<String> values = entry.getValue();
            count = count + values.size();
        }
        response.setResult(count);
    });

    private static ExpiredCache getCacheValue(LruCachePlus cache, String key) {
        ExpiredCache expiredCache = cache.getValue(key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.UNEXPIRED, key, TreeMultimap.create());
        }
        return expiredCache;
    }

    private static void flush(LruCachePlus cache, String key, Object value) {
        ExpiredCache expiredCache = getCacheValue(cache, key);
        expiredCache.setValue(value);
        cache.putValue(key, expiredCache);
    }

}
