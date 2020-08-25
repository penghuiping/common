package com.php25.common.core.mess;


/**
 * lru缓存实现，least recently used缓存算法
 *
 * @author penghuiping
 * @date 2020/8/25 13:51
 */
public interface LruCache<K, V> {

    /**
     * 放入缓存
     *
     * @param key   键
     * @param value 值
     */
    void putValue(K key, V value);

    /**
     * 如果不存在则放入缓存
     *
     * @param key   键
     * @param value 值
     */
    void putValueIfAbsent(K key, V value);

    /**
     * 通过键获取缓存值
     *
     * @param key 键
     * @return 值
     */
    V getValue(K key);

    /**
     * 获取当前缓存数量
     *
     * @return 缓存数量
     */
    int size();


}
