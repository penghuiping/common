package com.php25.common.core.mess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author penghuiping
 * @date 2020/8/25 13:47
 */
public class LruCacheImpl<K, V> extends LinkedHashMap<K, V> implements LruCache<K, V> {
    /**
     * 缓存最大数量
     */
    private int maxEntry;

    private ReadWriteLock readWriteLock;

    public LruCacheImpl(int maxEntry) {
        super(16, 0.75F, true);
        this.maxEntry = maxEntry;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        Lock lock = this.readWriteLock.readLock();
        lock.lock();
        try {
            return super.size() > maxEntry;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void putValue(K key, V value) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {
            super.put(key, value);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void putValueIfAbsent(K key, V value) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {
            super.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V getValue(K key) {
        Lock lock = this.readWriteLock.readLock();
        lock.lock();
        try {
            return super.getOrDefault(key, null);
        } finally {
            lock.unlock();
        }
    }
}
