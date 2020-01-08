package com.php25.common.redis;

import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/1/8 09:40
 */
public interface RSet<T> {

    /**
     * 把元素加入set
     * @param element 元素对象
     */
    void add(T element);

    /**
     * 获取set中的所有元素
     * @return set中的元素集
     */
    Set<T> members();

    /**
     * 判断此元素是否是set中的元素
     * @param element 元素对象
     * @return true:是set中的元素,false:不是set中的元素
     */
    Boolean isMember(T element);

    /**
     * 随机从set中取一个元素
     * @return 元素对象
     */
    T pop();
}
