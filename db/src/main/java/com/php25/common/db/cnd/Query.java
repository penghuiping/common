package com.php25.common.db.cnd;

import java.util.Collection;

/**
 *
 * @author: penghuiping
 * @Date: 2018/8/9 22:37
 */
public interface Query extends QueryExecute, QueryCondition, QueryOther {

    public Query whereEq(Class<?> modelClass, String column, Object value);

    public Query whereNotEq(Class<?> modelClass, String column, Object value);

    public Query whereGreat(Class<?> modelClass, String column, Object value);

    public Query whereGreatEq(Class<?> modelClass, String column, Object value);

    public Query whereLess(Class<?> modelClass, String column, Object value);

    public Query whereLessEq(Class<?> modelClass, String column, Object value);

    public Query whereLike(Class<?> modelClass, String column, String value);

    public Query whereNotLike(Class<?> modelClass, String column, String value);

    public Query whereIsNull(Class<?> modelClass, String column);

    public Query whereIsNotNull(Class<?> modelClass, String column);

    public Query whereIn(Class<?> modelClass, String column, Collection<?> value);

    public Query whereNotIn(Class<?> modelClass, String column, Collection<?> value);

    public Query whereBetween(Class<?> modelClass, String column, Object value1, Object value2);

    public Query whereNotBetween(Class<?> modelClass, String column, Object value1, Object value2);


    public Query andEq(Class<?> modelClass, String column, Object value);


    public Query andNotEq(Class<?> modelClass, String column, Object value);


    public Query andGreat(Class<?> modelClass, String column, Object value);


    public Query andGreatEq(Class<?> modelClass, String column, Object value);


    public Query andLess(Class<?> modelClass, String column, Object value);


    public Query andLessEq(Class<?> modelClass, String column, Object value);


    public Query andLike(Class<?> modelClass, String column, String value);


    public Query andNotLike(Class<?> modelClass, String column, String value);

    public Query andIsNull(Class<?> modelClass, String column);


    public Query andIsNotNull(Class<?> modelClass, String column);


    public Query andIn(Class<?> modelClass, String column, Collection<?> value);


    public Query andNotIn(Class<?> modelClass, String column, Collection<?> value);

    public Query andBetween(Class<?> modelClass, String column, Object value1, Object value2);

    public Query andNotBetween(Class<?> modelClass, String column, Object value1, Object value2);


    public Query orEq(Class<?> modelClass, String column, Object value);


    public Query orNotEq(Class<?> modelClass, String column, Object value);


    public Query orGreat(Class<?> modelClass, String column, Object value);


    public Query orGreatEq(Class<?> modelClass, String column, Object value);


    public Query orLess(Class<?> modelClass, String column, Object value);

    public Query orLessEq(Class<?> modelClass, String column, Object value);

    public Query orLike(Class<?> modelClass, String column, String value);


    public Query orNotLike(Class<?> modelClass, String column, String value);


    public Query orIsNull(Class<?> modelClass, String column);

    public Query orIsNotNull(Class<?> modelClass, String column);


    public Query orIn(Class<?> modelClass, String column, Collection<?> value);


    public Query orNotIn(Class<?> modelClass, String column, Collection<?> value);


    public Query orBetween(Class<?> modelClass, String column, Object value1, Object value2);


    public Query orNotBetween(Class<?> modelClass, String column, Object value1, Object value2);
}
