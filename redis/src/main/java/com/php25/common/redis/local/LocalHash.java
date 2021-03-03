package com.php25.common.redis.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RHash;

import java.util.HashMap;

/**
 * 模仿redis的api的本地hash实现
 *
 * @author penghuiping
 * @date 2021/2/24 16:58
 */
public class LocalHash<T> implements RHash<T> {

    private final String hashKey;

    private final HashMap<String, Object> hashMap;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    public LocalHash(String hashKey, Class<T> cls, LocalRedisManager redisManager) {
        this.hashKey = hashKey;
        this.redisManager = redisManager;
        this.cls = cls;
        this.hashMap = getInternalMap();
    }

    private HashMap<String, Object> getInternalMap() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.hashKey);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, this.hashKey, JsonUtil.toJson(new HashMap<>()));
        }
        return JsonUtil.fromJson(expiredCache.getValue().toString(), new TypeReference<>() {
        });
    }

    private void flush() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.hashKey);
        expiredCache.setValue(JsonUtil.toJson(this.hashMap));
        this.redisManager.cache.putValue(this.hashKey, expiredCache);
    }

    @Override
    public Boolean put(String key, T value) {
        this.hashMap.put(key, value);
        this.flush();
        return true;
    }

    @Override
    public T get(String key) {
        return (T) this.hashMap.get(key);
    }

    @Override
    public void delete(String key) {
        this.hashMap.remove(key);
        this.flush();
    }

    @Override
    public Long incr(String key) {
        Object val = this.hashMap.get(key);
        if (null == val) {
            val = 1L;
            this.hashMap.put(key, val);
            return (Long) val;
        }
        Long value = (Long) val;
        value = value + 1;
        this.hashMap.put(key, value);
        this.flush();
        return value;
    }

    @Override
    public Long decr(String key) {
        Object val = this.hashMap.get(key);
        if (null == val) {
            val = 1L;
            this.hashMap.put(key, val);
            return (Long) val;
        }
        Long value = (Long) val;
        value = value - 1;
        this.hashMap.put(key, value);
        this.flush();
        return value;
    }
}
