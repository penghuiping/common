package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.TimeUnit;

/**
 * redis缓存帮助类
 *
 * @author penghuiping
 * @Timer 2016/12/17.
 */
public interface RedisService {


    /**
     * 批量删除对应的value
     *
     * @param keys
     * @author penghuiping
     * @Timer 2016/12/17.
     */
    public void remove(final String... keys);


    /**
     * 删除对应的value
     *
     * @param key
     * @author penghuiping
     * @Timer 2016/12/17.
     */
    public void remove(final String key);

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     * @author penghuiping
     * @Timer 2016/12/17.
     */
    public boolean exists(final String key);

    /**
     * 读取缓存
     *
     * @param key
     * @param cls
     * @return
     * @author penghuiping
     * @Timer 2016/12/17.
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
     * @Timer 2016/12/17.
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
     * @Timer 2016/12/17.
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
     * 加锁
     *
     * @param redisKey   缓存KEY
     * @param expire     到期时间 毫秒
     * @param tryTimeout 尝试获取锁超时时间 毫秒
     * @return
     */
    public RedisLockInfo tryLock(String redisKey, long expire, long tryTimeout);

    /**
     * 解锁
     *
     * @param redisLockInfo 获取锁返回的对象
     * @return
     */
    public boolean releaseLock(RedisLockInfo redisLockInfo);
}
