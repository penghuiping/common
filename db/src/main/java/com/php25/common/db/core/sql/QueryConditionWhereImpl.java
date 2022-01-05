package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:47
 */
public class QueryConditionWhereImpl extends AbstractQuery implements QueryConditionWhere {

    public QueryConditionWhereImpl(QueryContext queryContext) {
        super(queryContext);
    }


    private Query andBaseAction(Column column, Object value, String op) {
        appendAndSql(column.toString(), value, op);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereOneEqualOne() {
        this.queryContext.getSql().append(" ").append(DbConstant.WHERE).append(" 1=1 ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query where(Query condition) {
        return manyCondition(condition, DbConstant.WHERE);
    }

    @Override
    public Query whereEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.EQ);
    }

    @Override
    public Query whereNotEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.NOT_EQ);
    }

    @Override
    public Query whereGreat(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.GREAT);
    }

    @Override
    public Query whereGreatEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.GREAT_EQ);
    }

    @Override
    public Query whereLess(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.LESS);
    }

    @Override
    public Query whereLessEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.LESS_EQ);
    }

    @Override
    public Query whereLike(Column column, String value) {
        return andBaseAction(column, value, DbConstant.LIKE);
    }

    @Override
    public Query whereNotLike(Column column, String value) {
        return andBaseAction(column, value, DbConstant.NOT_LIKE);
    }

    @Override
    public Query whereIsNull(Column column) {
        return andBaseAction(column, null, DbConstant.IS_NULL);
    }

    @Override
    public Query whereIsNotNull(Column column) {
        return andBaseAction(column, null, DbConstant.IS_NOT_NULL);
    }

    @Override
    public Query whereIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.IN, DbConstant.AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.NOT_IN, DbConstant.AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.BETWEEN, DbConstant.AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.NOT_BETWEEN, DbConstant.AND, value1, value2);
        return this.queryContext.getQuery();
    }
}
