package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:04
 */
public interface QueryConditionOr {
    /***
     * 多条件组合 or
     * @param condition 条件组合
     * @return 查询
     */
    Query or(Query condition);


    /**
     * where ... or column = value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orEq(Column column, Object value);

    /**
     * where ... or column != value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orNotEq(Column column, Object value);

    /**
     * where ... or column > value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orGreat(Column column, Object value);

    /**
     * where ... or column >= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orGreatEq(Column column, Object value);

    /**
     * where ... or column < value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orLess(Column column, Object value);

    /**
     * where ... or column <= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orLessEq(Column column, Object value);

    /**
     * where ... or column like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orLike(Column column, String value);

    /**
     * where ... or column not like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orNotLike(Column column, String value);

    /**
     * where ... or column is null
     *
     * @param column 字段
     * @return 查询
     */
    Query orIsNull(Column column);

    /**
     * where ... or column is not null
     *
     * @param column 字段
     * @return 查询
     */
    Query orIsNotNull(Column column);

    /**
     * where ... or column in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orIn(Column column, Collection<?> value);

    /**
     * where ... or column not in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query orNotIn(Column column, Collection<?> value);

    /**
     * where ... or between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段值2
     * @return 查询
     */
    Query orBetween(Column column, Object value1, Object value2);

    /**
     * where ... or column not between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段值2
     * @return 查询
     */
    Query orNotBetween(Column column, Object value1, Object value2);
}
