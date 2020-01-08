package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author penghuiping
 * @date 2020/1/4 17:13
 */
public interface RString {

    /**
     * 读取缓存
     *
     * @param key 读取缓存的key值
     * @param cls 把缓存值映射为指定的class类
     * @return 返回class的实例对象
     */
    <T> T get(final String key, Class<T> cls);

    /**
     * 读取缓存
     *
     * @param key 读取缓存的key值
     * @param cls 把缓存值映射为指定的class类
     * @param <T> 返回class的实例对象
     */
    <T> T get(final String key, TypeReference<T> cls);


    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存值
     * @return true:成功 false:失败
     */
    Boolean set(final String key, Object value);

    /**
     * 写入缓存,并且指定有效期
     *
     * @param key        缓存key
     * @param value      缓存值
     * @param expireTime 单位秒
     * @return true:成功 false:失败
     */
    Boolean set(final String key, Object value, Long expireTime);


    /**
     * 写入缓存,在key不存在的情况下写入缓存
     *
     * @param key   缓存key
     * @param value 缓存值
     * @return true:成功 false:失败
     */
    Boolean setNx(final String key, Object value);

    /**
     * 写入缓存,并且指定有效期,在key不存在的情况下写入缓存
     *
     * @param key        缓存key
     * @param value      缓存值
     * @param expireTime 单位秒
     * @return true:成功 false:失败
     */
    Boolean setNx(final String key, Object value, Long expireTime);

    /**
     * 线程安全的改变key对应的值，并且每调用一次值加1
     *
     * @param key
     * @return
     */
    Long incr(final String key);

    /**
     * 线程安全的改变key对应的值，并且每调用一次值减1
     *
     * @param key
     * @return
     */
    Long decr(final String key);


}
