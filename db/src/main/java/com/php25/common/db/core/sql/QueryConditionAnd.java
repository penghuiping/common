package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;
import com.php25.common.db.specification.SearchParamBuilder;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:02
 */
public interface QueryConditionAnd {
    /**
     * 多条件组合 and
     *
     * @param condition 条件组合
     * @return 查询
     */
    Query and(Query condition);


    /**
     * where ... and column = value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andEq(Column column, Object value);

    /**
     * where ... and column != value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andNotEq(Column column, Object value);

    /**
     * where ... and column > value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andGreat(Column column, Object value);

    /**
     * where ... and column >= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andGreatEq(Column column, Object value);

    /**
     * where ... and column < value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andLess(Column column, Object value);

    /**
     * where ... and column <= value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andLessEq(Column column, Object value);

    /**
     * where ... and column like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andLike(Column column, String value);

    /**
     * where ... and column not like value
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andNotLike(Column column, String value);

    /**
     * where ... and column is null
     *
     * @param column 字段
     * @return 查询
     */
    Query andIsNull(Column column);

    /**
     * where ... and column is not null
     *
     * @param column 字段
     * @return 查询
     */
    Query andIsNotNull(Column column);

    /**
     * where ... and column in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andIn(Column column, Collection<?> value);

    /**
     * where ... and column not in (value)
     *
     * @param column 字段
     * @param value  字段值
     * @return 查询
     */
    Query andNotIn(Column column, Collection<?> value);

    /**
     * where ... and column between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段值2
     * @return 查询
     */
    Query andBetween(Column column, Object value1, Object value2);

    /**
     * where ... and column not between value1 and value2
     *
     * @param column 字段
     * @param value1 字段值1
     * @param value2 字段值2
     * @return 查询
     */
    Query andNotBetween(Column column, Object value1, Object value2);


    /**
     * 通过searchParamBuilder来构造查询条件
     *
     * @param searchParamBuilder 查询条件构造器
     * @return 查询
     */
    Query andSearchParamBuilder(SearchParamBuilder searchParamBuilder);
}
