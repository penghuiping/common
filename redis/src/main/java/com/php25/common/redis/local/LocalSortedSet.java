package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RSortedSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalSortedSet<T> implements RSortedSet<T> {

    private final String setKey;

    private final TreeMultimap<Double, String> treeMap;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    public LocalSortedSet(String setKey, Class<T> cls, LocalRedisManager redisManager) {
        this.setKey = setKey;
        this.cls = cls;
        this.redisManager = redisManager;
        this.treeMap = TreeMultimap.create();
    }

    @Override
    public Boolean add(T t, double score) {
        treeMap.put(score, JsonUtil.toJson(t));
        return true;
    }

    @Override
    public Long size() {
        return (long) treeMap.size();
    }

    @Override
    public Set<T> range(long start, long end) {
        Iterator<Map.Entry<Double, Collection<String>>> iterator = treeMap.asMap().entrySet().iterator();
        int count = 0;
        Set<T> res = new LinkedHashSet<>();
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
        return res;
    }

    @Override
    public Set<T> reverseRange(long start, long end) {
        Iterator<Map.Entry<Double, Collection<String>>> iterator = treeMap.asMap().descendingMap().entrySet().iterator();
        int count = 0;
        Set<T> res = new LinkedHashSet<>();
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
        return res;
    }

    @Override
    public Set<T> rangeByScore(double min, double max) {
        SortedMap<Double, Collection<String>> map = treeMap.asMap().subMap(min, true, max, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        Set<T> res = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            Collection<String> values = entry.getValue();
            for (String value : values) {
                res.add(JsonUtil.fromJson(value, cls));
            }
        }
        return res;
    }

    @Override
    public Set<T> reverseRangeByScore(double min, double max) {
        SortedMap<Double, Collection<String>> map = treeMap.asMap().descendingMap().subMap(max, true, min, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        Set<T> res = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (int i = values.size() - 1; i >= 0; i--) {
                String value = values.get(i);
                res.add(JsonUtil.fromJson(value, cls));
            }
        }
        return res;
    }

    @Override
    public Long rank(T t) {
        Iterator<Map.Entry<Double, Collection<String>>> iterator = treeMap.asMap().entrySet().iterator();
        long count = 0;
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            List<String> values = Lists.newArrayList(entry.getValue());
            for (String value : values) {
                T val = JsonUtil.fromJson(value, cls);
                if (t.equals(val)) {
                    return count;
                }
                ++count;
            }
        }
        return count;
    }

    @Override
    public Long reverseRank(T t) {
        return (this.size() - 1) - rank(t);
    }

    @Override
    public Long removeRangeByScore(double min, double max) {
        SortedMap<Double, Collection<String>> map = treeMap.asMap().subMap(min, true, max, true);
        Iterator<Map.Entry<Double, Collection<String>>> iterator = map.entrySet().iterator();
        long count = 0L;
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<String>> entry = iterator.next();
            iterator.remove();
            Collection<String> values = entry.getValue();
            count = count + values.size();
        }
        return count;
    }
}
