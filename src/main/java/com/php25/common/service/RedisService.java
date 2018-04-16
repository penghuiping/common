package com.php25.common.service;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * redis缓存帮助类
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
     * @return
     * @author penghuiping
     * @Timer 2016/12/17.
     */
    public <T> T get(final String key, Class<T> cls);


    public <T> T get(final String key, TypeReference<T> cls);

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
     * @param key
     * @return
     */
    public Long remainTimeToLive(final String key);
}
