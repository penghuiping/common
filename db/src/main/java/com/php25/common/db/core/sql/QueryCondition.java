package com.php25.common.db.core.sql;


import java.util.Collection;
import java.util.List;

/**
 * @author penghuiping
 * @date 2018-08-23
 */
public interface QueryCondition {

    /**
     * where 1=1
     *
     * @return
     */
    Query whereOneEqualOne();

    /**
     * where column = value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereEq(String column, Object value);

    /**
     * where column != value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereNotEq(String column, Object value);

    /**
     * where column > value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereGreat(String column, Object value);

    /**
     * where column >= value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereGreatEq(String column, Object value);

    /**
     * where column < value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereLess(String column, Object value);

    /**
     * where column <= value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereLessEq(String column, Object value);

    /**
     * where column like value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereLike(String column, String value);

    /**
     * where column not like value
     *
     * @param column
     * @param value
     * @return
     */
    Query whereNotLike(String column, String value);

    /**
     * where column is null
     *
     * @param column
     * @return
     */
    Query whereIsNull(String column);

    /**
     * where column is not null
     *
     * @param column
     * @return
     */
    Query whereIsNotNull(String column);

    /**
     * where column in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query whereIn(String column, Collection<?> value);

    /**
     * where column is not in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query whereNotIn(String column, Collection<?> value);

    /**
     * where column between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query whereBetween(String column, Object value1, Object value2);

    /**
     * where column not between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query whereNotBetween(String column, Object value1, Object value2);

    /**
     * where ... and column = value
     *
     * @param column
     * @param value
     * @return
     */
    Query andEq(String column, Object value);

    /**
     * where ... and column != value
     *
     * @param column
     * @param value
     * @return
     */
    Query andNotEq(String column, Object value);

    /**
     * where ... and column > value
     *
     * @param column
     * @param value
     * @return
     */
    Query andGreat(String column, Object value);

    /**
     * where ... and column >= value
     *
     * @param column
     * @param value
     * @return
     */
    Query andGreatEq(String column, Object value);

    /**
     * where ... and column < value
     *
     * @param column
     * @param value
     * @return
     */
    Query andLess(String column, Object value);

    /**
     * where ... and column <= value
     *
     * @param column
     * @param value
     * @return
     */
    Query andLessEq(String column, Object value);

    /**
     * where ... and column like value
     *
     * @param column
     * @param value
     * @return
     */
    Query andLike(String column, String value);

    /**
     * where ... and column not like value
     *
     * @param column
     * @param value
     * @return
     */
    Query andNotLike(String column, String value);

    /**
     * where ... and column is null
     *
     * @param column
     * @return
     */
    Query andIsNull(String column);

    /**
     * where ... and column is not null
     *
     * @param column
     * @return
     */
    Query andIsNotNull(String column);

    /**
     * where ... and column in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query andIn(String column, Collection<?> value);

    /**
     * where ... and column not in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query andNotIn(String column, Collection<?> value);

    /**
     * where ... and column between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query andBetween(String column, Object value1, Object value2);

    /**
     * where ... and column not between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query andNotBetween(String column, Object value1, Object value2);


    /**
     * where ... or column = value
     *
     * @param column
     * @param value
     * @return
     */
    Query orEq(String column, Object value);

    /**
     * where ... or column != value
     *
     * @param column
     * @param value
     * @return
     */
    Query orNotEq(String column, Object value);

    /**
     * where ... or column > value
     *
     * @param column
     * @param value
     * @return
     */
    Query orGreat(String column, Object value);

    /**
     * where ... or column >= value
     *
     * @param column
     * @param value
     * @return
     */
    Query orGreatEq(String column, Object value);

    /**
     * where ... or column < value
     *
     * @param column
     * @param value
     * @return
     */
    Query orLess(String column, Object value);

    /**
     * where ... or column <= value
     *
     * @param column
     * @param value
     * @return
     */
    Query orLessEq(String column, Object value);

    /**
     * where ... or column like value
     *
     * @param column
     * @param value
     * @return
     */
    Query orLike(String column, String value);

    /**
     * where ... or column not like value
     *
     * @param column
     * @param value
     * @return
     */
    Query orNotLike(String column, String value);

    /**
     * where ... or column is null
     *
     * @param column
     * @return
     */
    Query orIsNull(String column);

    /**
     * where ... or column is not null
     *
     * @param column
     * @return
     */
    Query orIsNotNull(String column);

    /**
     * where ... or column in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query orIn(String column, Collection<?> value);

    /**
     * where ... or column not in (value)
     *
     * @param column
     * @param value
     * @return
     */
    Query orNotIn(String column, Collection<?> value);

    /**
     * where ... or between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query orBetween(String column, Object value1, Object value2);

    /**
     * where ... or column not between value1 and value2
     *
     * @param column
     * @param value1
     * @param value2
     * @return
     */
    Query orNotBetween(String column, Object value1, Object value2);


    /**
     * 多条件组合 and
     *
     * @param condition
     * @return
     */

    Query and(Query condition);

    /***
     * 多条件组合 or
     * @param condition
     * @return
     */

    Query or(Query condition);

    Query where(Query condition);


    /**
     * 获取sql
     *
     * @return
     */
    StringBuilder getSql();


    /***
     * 获取参数
     * @return
     */
    List<Object> getParams();
}
