package com.php25.common.redis;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis缓存帮助类
 *
 * @author penghuiping
 * @date 2020/1/6.
 */
public interface RedisManager {


    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    void remove(final String... keys);


    /**
     * 删除对应的value
     *
     * @param key
     */
    void remove(final String key);

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return boolean
     */
    Boolean exists(final String key);

    /**
     * 根据key获取存活时间
     *
     * @param key
     * @return 存货时长 单位秒
     */
    Long getExpire(final String key);

    /**
     * 设置一个key的存活时间
     *
     * @param key
     * @param expireTime
     * @param timeUnit
     * @return
     */
    Boolean expire(final String key, Long expireTime, TimeUnit timeUnit);

    /**
     * 设置一个key在指定日期时间上过期
     *
     * @param key
     * @param date
     * @return
     */
    Boolean expireAt(final String key, Date date);

    /**
     * 获取分布锁
     *
     * @param lockKey 锁名
     * @return
     */
    Lock lock(String lockKey);

    /**
     * 获取redis中string数据类型的相关操作对象
     *
     * @return
     */
    RString string();

    /**
     * 获取redis中Hash数据类型的相关操作对象
     *
     * @param hashKey
     * @return
     */
    RHash hash(String hashKey);

    /**
     * 获取redis中list数据类型的相关操作对象
     *
     * @param listKey
     * @return
     */
    <T> RList<T> list(String listKey,Class<T> cls);
}
