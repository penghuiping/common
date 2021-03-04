package com.php25.common.redis;

/**
 * @author penghuiping
 * @date 2020/1/4 17:13
 */
public interface RHash<T> {


    /**
     * 往redis的Hash结构中放入k、v键值对
     *
     * @param key   键
     * @param value 值
     * @return true:成功 false:失败
     */
    Boolean put(String key, T value);

    /**
     * 往redis的Hash结构中放入k、v键值对
     * 只有当key不存在的时候才会成功
     *
     * @param key   键
     * @param value 值
     * @return true:成功 false:失败
     */
    Boolean putIfAbsent(String key, T value);

    /**
     * 从redis的Hash结构中,根据key获取value
     *
     * @param key 键
     * @return value值
     */
    T get(String key);

    /**
     * 判断redis的Hash结构中，是否存在key
     *
     * @param key 键
     * @return true:存在
     */
    Boolean hasKey(String key);

    /**
     * 移除指定的key
     *
     * @param key 键
     */
    void delete(String key);

    /**
     * 线程安全的改变redis的Hash结构中key对应的值，并且每调用一次值加1
     *
     * @param key 键
     * @return 计数器值
     */
    Long incr(String key);

    /**
     * 线程安全的改变redis的Hash结构中key对应的值，并且每调用一次值减1
     *
     * @param key 键
     * @return 计数器值
     */
    Long decr(String key);


}
