package com.php25.common.redis;

/**
 * 利用概率的方式计算set集合中的大小,由于set集合元素都是不相同的，也叫集合的基
 *
 * add操作并不是真正的向集合中加入元素，所以占用的存储空间很小，最多12kb
 *
 * size操作 利用概率的方式计算set集合中的大小,错误率在1%
 *
 * @author penghuiping
 * @date 2020/1/9 10:22
 */
public interface RHyperLogLogs {

    /**
     * 向集合中添加元素
     * @param values
     */
    void add(Object ...values);


    /**
     * 计算集合中元素大小
     * @return
     */
    Long size();
}
