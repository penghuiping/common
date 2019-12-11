package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis缓存帮助类
 *
 * @author penghuiping
 * @date 2016/12/17.
 */
public interface RedisManager {


    /**
     * 批量删除对应的value
     *
     * @param keys
     * @author penghuiping
     * @date 2016/12/17.
     */
    public void remove(final String... keys);


    /**
     * 删除对应的value
     *
     * @param key
     * @author penghuiping
     * @date 2016/12/17.
     */
    public void remove(final String key);

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     * @author penghuiping
     * @date 2016/12/17.
     */
    public boolean exists(final String key);

    /**
     * 读取缓存
     *
     * @param key
     * @param cls
     * @return
     * @author penghuiping
     * @date 2016/12/17.
     */
    public <T> T get(final String key, Class<T> cls);

    /**
     * 读取缓存
     *
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T get(final String key, TypeReference<T> cls);

    /**
     * 在key不存在的情况下写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setNx(final String key, Object value);


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     * @author penghuiping
     * @date 2016/12/17.
     */
    public boolean set(final String key, Object value);

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 单位秒
     * @return
     * @author penghuiping
     * @date 2016/12/17.
     */
    public boolean set(final String key, Object value, Long expireTime);

    /**
     * 根据key获取存活时间
     *
     * @param key
     * @return
     */
    public Long remainTimeToLive(final String key);

    /**
     * 根据指定key获取自增的id
     *
     * @param key
     * @return
     */
    public Long incr(final String key);

    /**
     * 设置一个key的存活时间
     *
     * @param key
     * @param expireTime
     * @param timeUnit
     * @return
     */
    public Boolean expire(final String key, Long expireTime, TimeUnit timeUnit);

    /**
     * 设置一个key在指定日期时间上过期
     *
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(final String key, Date date);


    /**
     * 获取分布锁
     *
     * @param lockKey 锁名
     * @return
     */
    public Lock obtainDistributeLock(String lockKey);

    /**
     * 往map结构中设置数据
     *
     * @param mapKey map结构的key
     * @param key    map中的一个元素的键
     * @param value  map中一个元素的值
     * @return
     */
    public Boolean setIntoMap(final String mapKey, String key, Object value);

    /**
     * 根据key获取map值
     *
     * @param mapKey
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getFromMap(final String mapKey, String key, Class<T> cls);

    /**
     * 根据key获取map值
     *
     * @param mapKey
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getFromMap(final String mapKey, String key, TypeReference<T> cls);

}