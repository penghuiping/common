package com.php25.common.db.cnd;

import com.php25.common.db.manager.JdbcModelManager;

import java.util.Collection;

/**
 * @author: penghuiping
 * @date: 2018/9/4 23:24
 * @description:
 */
public abstract class AbstractNewQuery extends AbstractQuery {
    Class clazz;

    @Override
    public String getCol(String name) {
        return getCol(clazz, name);
    }

    protected String getCol(Class<?> modelClass, String name) {
        try {
            if (!clazz.equals(modelClass)) {
                return " b." + JdbcModelManager.getDbColumnByClassColumn(modelClass, name) + " ";
            } else {
                return " a." + JdbcModelManager.getDbColumnByClassColumn(this.clazz, name) + " ";
            }
        } catch (Exception e) {
            //"无法通过jpa注解找到对应的column,直接调用父类的方法"
            return super.getCol(name);
        }
    }

    @Override
    public Query whereEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereEq(column, value);
    }

    @Override
    public Query whereNotEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereNotEq(column, value);
    }

    @Override
    public Query whereGreat(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereGreat(column, value);
    }

    @Override
    public Query whereGreatEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereGreatEq(column, value);
    }

    @Override
    public Query whereLess(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereLess(column, value);
    }

    @Override
    public Query whereLessEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.whereLessEq(column, value);
    }

    @Override
    public Query whereLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.whereLike(column, value);
    }

    @Override
    public Query whereNotLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.whereNotLike(column, value);
    }

    @Override
    public Query whereIsNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.whereIsNull(column);
    }

    @Override
    public Query whereIsNotNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.whereIsNotNull(column);
    }

    @Override
    public Query whereIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.whereIn(column, value);
    }

    @Override
    public Query whereNotIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.whereNotIn(column, value);
    }

    @Override
    public Query whereBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.whereBetween(column, value1, value2);
    }

    @Override
    public Query whereNotBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.whereNotBetween(column, value1, value2);
    }

    @Override
    public Query andEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andEq(column, value);
    }

    @Override
    public Query andNotEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andNotEq(column, value);
    }

    @Override
    public Query andGreat(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andGreat(column, value);
    }

    @Override
    public Query andGreatEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andGreatEq(column, value);
    }

    @Override
    public Query andLess(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andLess(column, value);
    }

    @Override
    public Query andLessEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.andLessEq(column, value);
    }

    @Override
    public Query andLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.andLike(column, value);
    }

    @Override
    public Query andNotLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.andNotLike(column, value);
    }

    @Override
    public Query andIsNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.andIsNull(column);
    }

    @Override
    public Query andIsNotNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.andIsNotNull(column);
    }

    @Override
    public Query andIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.andIn(column, value);
    }

    @Override
    public Query andNotIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.andNotIn(column, value);
    }

    @Override
    public Query andBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.andBetween(column, value1, value2);
    }

    @Override
    public Query andNotBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.andNotBetween(column, value1, value2);
    }

    @Override
    public Query orEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orEq(column, value);
    }

    @Override
    public Query orNotEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orNotEq(column, value);
    }

    @Override
    public Query orGreat(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orGreat(column, value);
    }

    @Override
    public Query orGreatEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orGreatEq(column, value);
    }

    @Override
    public Query orLess(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orLess(column, value);
    }

    @Override
    public Query orLessEq(Class<?> modelClass, String column, Object value) {
        column = getCol(modelClass, column);
        return super.orLessEq(column, value);
    }

    @Override
    public Query orLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.orLike(column, value);
    }

    @Override
    public Query orNotLike(Class<?> modelClass, String column, String value) {
        column = getCol(modelClass, column);
        return super.orNotLike(column, value);
    }

    @Override
    public Query orIsNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.orIsNull(column);
    }

    @Override
    public Query orIsNotNull(Class<?> modelClass, String column) {
        column = getCol(modelClass, column);
        return super.orIsNotNull(column);
    }

    @Override
    public Query orIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.orIn(column, value);
    }

    @Override
    public Query orNotIn(Class<?> modelClass, String column, Collection<?> value) {
        column = getCol(modelClass, column);
        return super.orNotIn(column, value);
    }

    @Override
    public Query orBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.orBetween(column, value1, value2);
    }

    @Override
    public Query orNotBetween(Class<?> modelClass, String column, Object value1, Object value2) {
        column = getCol(modelClass, column);
        return super.orNotBetween(column, value1, value2);
    }
}
