package com.php25.common.redis.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 模仿redis的api的本地list实现
 *
 * @author penghuiping
 * @date 2021/2/24 16:59
 */
public class LocalList<T> implements RList<T> {

    private final String listKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    private LinkedList<T> list;

    public LocalList(String listKey, Class<T> cls, LocalRedisManager redisManager) {
        this.listKey = listKey;
        this.redisManager = redisManager;
        this.cls = cls;
        this.list = getInternalList();
    }

    private LinkedList<T> getInternalList() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.listKey);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, this.listKey, JsonUtil.toJson(new LinkedList<>()));
        }
        return JsonUtil.fromJson(expiredCache.getValue().toString(), new TypeReference<>() {
        });
    }

    private void flush() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.listKey);
        expiredCache.setValue(JsonUtil.toJson(this.list));
        this.redisManager.cache.putValue(this.listKey, expiredCache);
    }

    @Override
    public Long rightPush(T value) {
        this.list.addLast(value);
        this.flush();
        return (long) this.list.size();
    }

    @Override
    public Long leftPush(T value) {
        this.list.addFirst(value);
        this.flush();
        return (long) this.list.size();
    }

    @Override
    public T rightPop() {
        T res = this.list.removeLast();
        this.flush();
        return res;
    }

    @Override
    public T leftPop() {
        T res = this.list.removeFirst();
        this.flush();
        return res;
    }

    @Override
    public List<T> leftRange(long start, long end) {
        return this.list.subList((int) start, (int) end);
    }

    @Override
    public void leftTrim(long start, long end) {
        this.list = (LinkedList<T>) this.list.subList((int) start, (int) end);
        this.flush();
    }

    @Override
    public Long size() {
        return (long) this.list.size();
    }

    @Override
    public T blockLeftPop(long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public T blockRightPop(long timeout, TimeUnit timeUnit) {
        return null;
    }
}
