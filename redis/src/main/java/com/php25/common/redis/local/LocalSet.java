package com.php25.common.redis.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalSet<T> implements RSet<T> {

    private final String setKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    private final HashSet<T> set;

    public LocalSet(String setKey, Class<T> cls, LocalRedisManager redisManager) {
        this.setKey = setKey;
        this.redisManager = redisManager;
        this.cls = cls;
        this.set = getInternalSet();
    }


    private HashSet<T> getInternalSet() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.setKey);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, this.setKey, JsonUtil.toJson(new HashSet<>()));
        }
        return JsonUtil.fromJson(expiredCache.getValue().toString(), new TypeReference<>() {
        });
    }

    private void flush() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.setKey);
        expiredCache.setValue(JsonUtil.toJson(this.set));
        this.redisManager.cache.putValue(this.setKey, expiredCache);
    }

    @Override
    public void add(T element) {
        this.set.add(element);
        this.flush();
    }

    @Override
    public void remove(T element) {
        this.set.remove(element);
        this.flush();
    }

    @Override
    public Set<T> members() {
        return this.set;
    }

    @Override
    public Boolean isMember(T element) {
        return this.set.contains(element);
    }

    @Override
    public T pop() {
        Iterator<T> iterator = this.set.iterator();
        if (iterator.hasNext()) {
            T val = iterator.next();
            this.set.remove(val);
            this.flush();
            return val;
        }
        return null;
    }

    @Override
    public Set<T> union(String otherSetKey) {
        RSet<T> rSet = this.redisManager.set(otherSetKey, cls);
        Set<T> otherSet = rSet.members();
        this.set.addAll(otherSet);
        this.flush();
        return this.set;
    }

    @Override
    public Set<T> inter(String otherSetKey) {
        RSet<T> rSet = this.redisManager.set(otherSetKey, cls);
        Set<T> otherSet = rSet.members();
        this.set.retainAll(otherSet);
        this.flush();
        return this.set;
    }

    @Override
    public Set<T> diff(String otherSetKey) {
        RSet<T> rSet = this.redisManager.set(otherSetKey, cls);
        Set<T> otherSet = rSet.members();
        this.set.removeAll(otherSet);
        this.flush();
        return this.set;
    }

    @Override
    public Long size() {
        return (long) this.set.size();
    }

    @Override
    public T getRandomMember() {
        Iterator<T> iterator = this.set.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
