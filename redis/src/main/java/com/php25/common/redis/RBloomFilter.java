package com.php25.common.redis;

/**
 * @author penghuiping
 * @date 2020/1/21 17:59
 */
public interface RBloomFilter {

    /**
     * 判断keys是否存在于集合
     */
    boolean mightContain(String key);

    /**
     * 将key存入redis bitmap
     */
    void put(String key);

}
