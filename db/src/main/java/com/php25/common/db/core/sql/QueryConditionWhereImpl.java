package com.php25.common.db.core.sql;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:47
 */
public class QueryConditionWhereImpl extends AbstractQuery implements QueryConditionWhere {

    public QueryConditionWhereImpl(QueryContext queryContext) {
        super(queryContext);
    }

    @Override
    public Query whereOneEqualOne() {
        this.queryContext.getSql().append(" ").append(WHERE).append(" 1=1 ");
        return this.queryContext.getQuery();
    }


    @Override
    public Query whereEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereGreat(String column, Object value) {
        appendAndSql(column, value, ">");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereGreatEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereLess(String column, Object value) {
        appendAndSql(column, value, "<");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereLessEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereLike(String column, String value) {
        appendAndSql(column, value, "LIKE ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereIsNull(String column) {
        appendAndSql(column, null, "IS NULL ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query whereNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query where(Query condition) {
        return manyCondition(condition, WHERE);
    }

    @Override
    public Query whereEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereNotEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereGreat(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereGreatEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereLess(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereLessEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query whereLike(Column column, String value) {
        return null;
    }

    @Override
    public Query whereNotLike(Column column, String value) {
        return null;
    }

    @Override
    public Query whereIsNull(Column column) {
        return null;
    }

    @Override
    public Query whereIsNotNull(Column column) {
        return null;
    }

    @Override
    public Query whereIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query whereNotIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query whereBetween(Column column, Object value1, Object value2) {
        return null;
    }

    @Override
    public Query whereNotBetween(Column column, Object value1, Object value2) {
        return null;
    }
}
