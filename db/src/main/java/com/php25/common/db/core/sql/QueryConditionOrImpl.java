package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:51
 */
public class QueryConditionOrImpl extends AbstractQuery implements QueryConditionOr {

    public QueryConditionOrImpl(QueryContext queryContext) {
        super(queryContext);
    }


    private Query orBaseAction(Column column, Object value, String op) {
        appendOrSql(column.toString(), value, op);
        return this.queryContext.getQuery();
    }

    @Override
    public Query or(Query condition) {
        return manyCondition(condition, DbConstant.OR);
    }

    @Override
    public Query orEq(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.EQ);
    }

    @Override
    public Query orNotEq(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.NOT_EQ);
    }

    @Override
    public Query orGreat(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.GREAT);
    }

    @Override
    public Query orGreatEq(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.GREAT_EQ);
    }

    @Override
    public Query orLess(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.LESS);
    }

    @Override
    public Query orLessEq(Column column, Object value) {
        return orBaseAction(column, value, DbConstant.LESS_EQ);
    }

    @Override
    public Query orLike(Column column, String value) {
        return orBaseAction(column, value, DbConstant.LIKE);
    }

    @Override
    public Query orNotLike(Column column, String value) {
        return orBaseAction(column, value, DbConstant.NOT_LIKE);
    }

    @Override
    public Query orIsNull(Column column) {
        return orBaseAction(column, null, DbConstant.IS_NULL);
    }

    @Override
    public Query orIsNotNull(Column column) {
        return orBaseAction(column, null, DbConstant.IS_NOT_NULL);
    }

    @Override
    public Query orIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.IN, DbConstant.OR);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.NOT_IN, DbConstant.OR);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.BETWEEN, DbConstant.OR, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.NOT_BETWEEN, DbConstant.OR, value1, value2);
        return this.queryContext.getQuery();
    }
}
