package com.php25.common.db.core.sql;

import com.php25.common.db.core.sql.column.Column;
import com.php25.common.db.specification.Operator;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;

import java.util.Collection;
import java.util.List;

import static com.php25.common.db.core.sql.column.Columns.col;

/**
 * @author penghuiping
 * @date 2021/12/26 15:43
 */
public class QueryConditionAndImpl extends AbstractQuery implements QueryConditionAnd {
    public QueryConditionAndImpl(QueryContext queryContext) {
        super(queryContext);
    }

    private Query andBaseAction(Column column, Object value, String op) {
        appendAndSql(column.toString(), value, op);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.EQ);
    }

    @Override
    public Query andNotEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.NOT_EQ);
    }

    @Override
    public Query andGreat(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.GREAT);
    }

    @Override
    public Query andGreatEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.GREAT_EQ);
    }

    @Override
    public Query andLess(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.LESS);
    }

    @Override
    public Query andLessEq(Column column, Object value) {
        return andBaseAction(column, value, DbConstant.LESS_EQ);
    }

    @Override
    public Query andLike(Column column, String value) {
        return andBaseAction(column, value, DbConstant.LIKE);
    }

    @Override
    public Query andNotLike(Column column, String value) {
        return andBaseAction(column, value, DbConstant.NOT_LIKE);
    }

    @Override
    public Query andIsNull(Column column) {
        return andBaseAction(column, null, DbConstant.IS_NULL);
    }

    @Override
    public Query andIsNotNull(Column column) {
        return andBaseAction(column, null, DbConstant.IS_NOT_NULL);
    }

    @Override
    public Query andIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.IN, DbConstant.AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotIn(Column column, Collection<?> value) {
        appendInSql(column.toString(), value, DbConstant.NOT_IN, DbConstant.AND);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.BETWEEN, DbConstant.AND, value1, value2);
        return this.queryContext.getQuery();
    }

    @Override
    public Query andNotBetween(Column column, Object value1, Object value2) {
        appendBetweenSql(column.toString(), DbConstant.NOT_BETWEEN, DbConstant.AND, value1, value2);
        return this.queryContext.getQuery();
    }


    @Override
    public Query and(Query condition) {
        return manyCondition(condition, DbConstant.AND);
    }

    @Override
    public Query andSearchParamBuilder(SearchParamBuilder searchParamBuilder) {
        List<SearchParam> searchParams = searchParamBuilder.build();
        for (SearchParam searchParam : searchParams) {
            Operator operator = searchParam.getOperator();
            if (null != operator) {
                switch (operator) {
                    case EQ:
                        this.andEq(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case NE:
                        this.andNotEq(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case LIKE:
                        this.andLike(col(searchParam.getFieldName()), (String) searchParam.getValue());
                        break;
                    case GT:
                        this.andGreat(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case LT:
                        this.andLess(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case GTE:
                        this.andGreatEq(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case LTE:
                        this.andLessEq(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                    case IN:
                        this.andIn(col(searchParam.getFieldName()), (Collection<?>) searchParam.getValue());
                        break;
                    case NIN:
                        this.andNotIn(col(searchParam.getFieldName()), (Collection<?>) searchParam.getValue());
                        break;
                    default:
                        this.andEq(col(searchParam.getFieldName()), searchParam.getValue());
                        break;
                }
            }
        }
        return this.queryContext.getQuery();
    }
}
