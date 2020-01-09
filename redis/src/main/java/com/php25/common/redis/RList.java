package com.php25.common.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/1/4 17:14
 */
public interface RList<T> {


    /**
     * 往redis的list数据类型中插入元素，从list的最右边放入元素
     *
     * @param value 放入的元素值
     * @return list的长度
     */
    Long rightPush(T value);

    /**
     * 往redis的list数据类型中插入元素，从list的最左边放入元素
     *
     * @param value 放入的元素值
     * @return list的长度
     */
    Long leftPush(T value);


    /**
     * 从redis的list中右边取出一个元素
     *
     * @return 最右边的元素
     */
    T rightPop();

    /**
     * 从redis的list左边取出一个元素
     *
     * @return 最左边的元素
     */
    T leftPop();

    /**
     * 从redis的list中指定位置范围取出元素
     *
     * @param start 开始位置
     * @param end   结束位置
     * @return 满足条件的数据集合
     */
    List<T> leftRange(long start, long end);

    /**
     * 把redis中的list中的元素截取，从start位置开始截取，直到end位置结束 (可用于展示最新数据记录)
     *
     * @param start 开始位置
     * @param end   结束位置
     */
    void leftTrim(long start, long end);

    /**
     * 获取redis中list的长度
     *
     * @return
     */
    Long size();

    /**
     * 阻塞版lpop(可用于进程间通信)
     *
     * @param timeout
     * @param timeUnit
     * @return
     */
    T blockLeftPop(long timeout, TimeUnit timeUnit);

    /**
     * 阻塞版rpop (可用于进程间通信)
     *
     * @param timeout
     * @param timeUnit
     * @return
     */
    T blockRightPop(long timeout, TimeUnit timeUnit);

}
