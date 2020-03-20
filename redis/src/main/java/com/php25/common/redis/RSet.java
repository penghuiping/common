package com.php25.common.redis;

import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/1/8 09:40
 */
public interface RSet<T> {

    /**
     * 把元素加入set
     *
     * @param element 元素对象
     */
    void add(T element);

    /**
     * 移除元素
     * @param element 元素对象
     */
    void remove(T element);

    /**
     * 获取set中的所有元素
     *
     * @return set中的元素集
     */
    Set<T> members();

    /**
     * 判断此元素是否是set中的元素
     *
     * @param element 元素对象
     * @return true:是set中的元素,false:不是set中的元素
     */
    Boolean isMember(T element);

    /**
     * 随机从set中取一个元素，并且元素会从集合中移除
     *
     * @return 元素对象
     */
    T pop();

    /**
     * set 合并操作
     *
     * @param otherSetKey 其他set集合的key值
     * @return 获取并集数据
     */
    Set<T> union(String otherSetKey);

    /**
     * set 交集操作
     *
     * @param otherSetKey 其他set集合的key值
     * @return 获取交集数据
     */
    Set<T> inter(String otherSetKey);

    /**
     * set 差集操作 从本集合中己移除所有包含在 otherSetKey 集合中的元素
     *
     * @param otherSetKey 其他set集合的key值
     * @return 获取差集数据
     */
    Set<T> diff(String otherSetKey);

    /**
     * 获取集合的大小
     *
     * @return 集合大小
     */
    Long size();

    /**
     * 随机获取一个set中的元素，并且不会把元素从集合中移除
     *
     * @return 随机的set中的元素
     */
    T getRandomMember();
}
