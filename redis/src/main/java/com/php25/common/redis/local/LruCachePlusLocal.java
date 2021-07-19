package com.php25.common.redis.local;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/2/25 15:49
 */
class LruCachePlusLocal extends LinkedHashMap<String, ExpiredCache> implements LruCachePlus {

    /**
     * 缓存最大数量
     */
    private final int maxEntry;

    public LruCachePlusLocal(int maxEntry) {
        super(16, 0.75F, true);
        this.maxEntry = maxEntry;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, ExpiredCache> eldest) {
        return super.size() > maxEntry;
    }

    @Override
    public void putValue(String key, ExpiredCache value) {
        this.put(key, value);
    }

    @Override
    public void putValueIfAbsent(String key, ExpiredCache value) {
        this.putIfAbsent(key, value);
    }

    @Override
    public ExpiredCache getValue(String key) {
        ExpiredCache expiredCache = this.get(key);
        if (null != expiredCache) {
            if (expiredCache.isExpired()) {
                //过期的缓存
                this.remove(key);
                return null;
            }
        }
        return expiredCache;
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean remove(String key) {
        super.remove(key);
        return true;
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(key);
    }
}
