package com.php25.common.redis;

import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/1/9 10:21
 */
public interface RSortedSet<T> {

    /**
     * 新增一个元素，如果新增的元素已经存在则相当于更新操作
     *
     * @param t     元素
     * @param score 分数
     * @return true:成功 false:失败
     */
    Boolean add(T t, double score);

    /**
     * 返回集合元素数量
     *
     * @return 集合元素数量
     */
    Long size();

    /**
     * 范围查询
     *
     * @param start 起点位置
     * @param end   结束位置
     * @return 符合查询条件的元素集合
     */
    Set<T> range(long start, long end);

    /**
     * 范围查询查询，反向结果
     *
     * @param start 起点位置
     * @param end   结束位置
     * @return 符合查询条件的元素集合
     */
    Set<T> reverseRange(long start, long end);

    /**
     * 根据分数范围查询
     *
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 符合查询条件的元素集合
     */
    Set<T> rangeByScore(double min, double max);

    /**
     * 根据分数范围查询，反向结果
     *
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 符合查询条件的元素集合
     */
    Set<T> reverseRangeByScore(double min, double max);

    /**
     * 返回元素在集合中的排名
     *
     * @param t 元素
     * @return 排名
     */
    Long rank(T t);

    /**
     * 返回元素在集合中的反向排名
     *
     * @param t 元素
     * @return 排名
     */
    Long reverseRank(T t);

    /**
     * 根据分数范围移除集合中的元素
     *
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 移除的元素数量
     */
    Long removeRangeByScore(double min, double max);
}
