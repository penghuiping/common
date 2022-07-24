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
     */
    <T> RHash<T> hash(String hashKey, Class<T> cls);

    /**
     * 获取redis中list数据类型的相关操作对象
     */
    <T> RList<T> list(String listKey, Class<T> cls);

    /**
     * 获取redis中set数据类型的相关操作对象
     */
    <T> RSet<T> set(String setKey, Class<T> cls);

    /**
     * 获取redis中zset数据类型的相关操作对象
     */
    <T> RSortedSet<T> zset(String setKey, Class<T> cls);

    /**
     * 获取布隆过滤器操作对象
     *
     * @param name               过滤器的名称，对应redis string类型的key
     * @param expectedInsertions 预计插入量
     * @param fpp                可接受错误率
     * @return
     */
    RBloomFilter bloomFilter(String name, long expectedInsertions, double fpp);

    /**
     * 获取 RHyperLogLogs 操作对象
     *
     * @param key
     * @return
     */
    RHyperLogLogs hyperLogLogs(String key);

    /**
     * 获取限流器，使用令牌桶算法
     *
     * @param rate 令牌增长率，单位时间(秒)内向令牌桶里添加令牌的数量 ,
     *             how many requests per second will be allowed to visit your system.
     * @param id   令牌桶唯一标识
     * @return
     */
    RRateLimiter rateLimiter(int rate, String id);

    /**
     * 每天一次的序列号
     *
     * @param prefix 前缀
     * @return 序列号 yyyyMMdd000000000000
     */
    String getDatetimeSequenceNumber(String prefix);
}
