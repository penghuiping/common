package com.php25.common.db.core.sql;

import java.util.Collection;

/**
 * @author penghuiping
 * @date 2021/12/26 15:51
 */
public class QueryConditionOrImpl extends AbstractQuery implements QueryConditionOr {

    public QueryConditionOrImpl(QueryContext queryContext) {
        super(queryContext);
    }

    @Override
    public Query orEq(String column, Object value) {
        appendOrSql(column, value, "=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotEq(String column, Object value) {
        appendOrSql(column, value, "<>");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orGreat(String column, Object value) {
        appendOrSql(column, value, ">");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orGreatEq(String column, Object value) {
        appendOrSql(column, value, ">=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orLess(String column, Object value) {
        appendOrSql(column, value, "<");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orLessEq(String column, Object value) {
        appendOrSql(column, value, "<=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orLike(String column, String value) {
        appendOrSql(column, value, "LIKE");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotLike(String column, String value) {
        appendOrSql(column, value, "NOT LIKE");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orIsNull(String column) {
        appendOrSql(column, null, "IS NULL");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orIsNotNull(String column) {
        appendOrSql(column, null, "IS NOT NULL");
        return this.queryContext.getQuery();
    }

    @Override
    public Query orIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, OR);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, OR);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, OR, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query orNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, OR, value1, value2);
        return this.queryContext.getQuery();
    }


    @Override
    public Query or(Query condition) {
        return manyCondition(condition, OR);
    }

    @Override
    public Query orEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query orNotEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query orGreat(Column column, Object value) {
        return null;
    }

    @Override
    public Query orGreatEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query orLess(Column column, Object value) {
        return null;
    }

    @Override
    public Query orLessEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query orLike(Column column, String value) {
        return null;
    }

    @Override
    public Query orNotLike(Column column, String value) {
        return null;
    }

    @Override
    public Query orIsNull(Column column) {
        return null;
    }

    @Override
    public Query orIsNotNull(Column column) {
        return null;
    }

    @Override
    public Query orIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query orNotIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query orBetween(Column column, Object value1, Object value2) {
        return null;
    }

    @Override
    public Query orNotBetween(Column column, Object value1, Object value2) {
        return null;
    }
}
