package com.php25.common.db.core.sql;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;

import java.util.Collection;
import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/26 15:43
 */
public class QueryConditionAndImpl extends AbstractQuery implements QueryConditionAnd {

    public QueryConditionAndImpl(QueryContext queryContext) {
        super(queryContext);
    }

    @Override
    public Query andEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andGreat(String column, Object value) {
        appendAndSql(column, value, ">");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andGreatEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andLess(String column, Object value) {
        appendAndSql(column, value, "<");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andLessEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andLike(String column, String value) {
        appendAndSql(column, value, "LIKE ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andIsNull(String column) {
        appendAndSql(column, null, "IS NULL ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL ");
        return this.queryContext.getQuery();
    }

    @Override
    public Query andIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query and(Query condition) {
        return manyCondition(condition, AND);
    }


    @Override
    public Query andSearchParamBuilder(SearchParamBuilder searchParamBuilder) {
        List<SearchParam> searchParams = searchParamBuilder.build();
        for (SearchParam searchParam : searchParams) {
            String operator = searchParam.getOperator().name();
            if (!StringUtil.isBlank(operator)) {
                if ("eq".equalsIgnoreCase(operator)) {
                    this.andEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("ne".equalsIgnoreCase(operator)) {
                    this.andNotEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("like".equalsIgnoreCase(operator)) {
                    this.andLike(searchParam.getFieldName(), (String) searchParam.getValue());
                } else if ("gt".equalsIgnoreCase(operator)) {
                    this.andGreat(searchParam.getFieldName(), searchParam.getValue());
                } else if ("lt".equalsIgnoreCase(operator)) {
                    this.andLess(searchParam.getFieldName(), searchParam.getValue());
                } else if ("gte".equalsIgnoreCase(operator)) {
                    this.andGreatEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("lte".equalsIgnoreCase(operator)) {
                    this.andLessEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("in".equalsIgnoreCase(operator)) {
                    this.andIn(searchParam.getFieldName(), (Collection<?>) searchParam.getValue());
                } else if ("nin".equalsIgnoreCase(operator)) {
                    this.andNotIn(searchParam.getFieldName(), (Collection<?>) searchParam.getValue());
                } else {
                    this.andEq(searchParam.getFieldName(), searchParam.getValue());
                }
            }
        }
        return this.queryContext.getQuery();
    }

    @Override
    public Query andEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query andNotEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query andGreat(Column column, Object value) {
        return null;
    }

    @Override
    public Query andGreatEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query andLess(Column column, Object value) {
        return null;
    }

    @Override
    public Query andLessEq(Column column, Object value) {
        return null;
    }

    @Override
    public Query andLike(Column column, String value) {
        return null;
    }

    @Override
    public Query andNotLike(Column column, String value) {
        return null;
    }

    @Override
    public Query andIsNull(Column column) {
        return null;
    }

    @Override
    public Query andIsNotNull(Column column) {
        return null;
    }

    @Override
    public Query andIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query andNotIn(Column column, Collection<?> value) {
        return null;
    }

    @Override
    public Query andBetween(Column column, Object value1, Object value2) {
        return null;
    }

    @Override
    public Query andNotBetween(Column column, Object value1, Object value2) {
        return null;
    }
}
