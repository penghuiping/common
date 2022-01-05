package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 14:59
 */
public interface QueryConditionWhere {
    /**
     * where 1=1
     *
     * @return 查询
     */
    Query whereOneEqualOne();

    /***
     * 多条件组合
     * @param condition 条件组合
     * @return 查询
     */
    Query where(Query condition);


    /**
     * where column = value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereEq(Column column, Object value);

    /**
     * where column != value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereNotEq(Column column, Object value);

    /**
     * where column > value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereGreat(Column column, Object value);

    /**
     * where column >= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereGreatEq(Column column, Object value);

    /**
     * where column < value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereLess(Column column, Object value);

    /**
     * where column <= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereLessEq(Column column, Object value);

    /**
     * where column like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereLike(Column column, String value);

    /**
     * where column not like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereNotLike(Column column, String value);

    /**
     * where column is null
     *
     * @param column 字段
     * @return 查询
     */
    Query whereIsNull(Column column);

    /**
     * where column is not null
     *
     * @param column 字段
     * @return 查询
     */
    Query whereIsNotNull(Column column);

    /**
     * where column in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereIn(Column column, Collection<?> value);

    /**
     * where column is not in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query whereNotIn(Column column, Collection<?> value);

    /**
     * where column between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段值2
     * @return 查询
     */
    Query whereBetween(Column column, Object value1, Object value2);

    /**
     * where column not between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段2
     * @return 查询
     */
    Query whereNotBetween(Column column, Object value1, Object value2);
}
