package com.php25.common.db.core.sql;

import com.php25.common.db.specification.SearchParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/2 13:32
 */
public abstract class BaseQuery implements Query {
    private static final Logger log = LoggerFactory.getLogger(BaseQuery.class);

    protected final QueryContext queryContext;
    private final QueryAction queryAction;
    private final QueryConditionAnd queryConditionAnd;
    private final QueryConditionOr queryConditionOr;
    private final QueryConditionWhere queryConditionWhere;
    private final QueryOther queryOther;

    public BaseQuery() {
        this.queryContext = new QueryContext(this);
        this.queryAction = new QueryActionImpl(queryContext);
        this.queryConditionAnd = new QueryConditionAndImpl(queryContext);
        this.queryConditionOr = new QueryConditionOrImpl(queryContext);
        this.queryConditionWhere = new QueryConditionWhereImpl(queryContext);
        this.queryOther = new QueryOtherImpl(queryContext);
    }

    @Override
    public List<Object> getParams() {
        return this.queryContext.getParams();
    }

    @Override
    public StringBuilder getSql() {
        return this.queryContext.getSql();
    }

    @Override
    public SqlParams select(Class<?> model, String... columns) {
        return this.queryAction.select(model, columns);
    }

    @Override
    public SqlParams select(String... columns) {
        return this.queryAction.select(columns);
    }

    @Override
    public SqlParams single() {
        return this.queryAction.single();
    }

    @Override
    public SqlParams select() {
        return this.queryAction.select();
    }

    @Override
    public <M> SqlParams insert(M model) {
        return this.queryAction.insert(model);
    }

    @Override
    public <M> SqlParams insertIncludeNull(M model) {
        return this.queryAction.insertIncludeNull(model);
    }

    @Override
    public <M> SqlParams insertBatch(List<M> models) {
        return this.queryAction.insertBatch(models);
    }

    @Override
    public <M> SqlParams update(M model) {
        return this.queryAction.update(model);
    }

    @Override
    public <M> SqlParams updateIncludeNull(M model) {
        return this.queryAction.updateIncludeNull(model);
    }

    @Override
    public <M> SqlParams updateBatch(List<M> models) {
        return this.queryAction.updateBatch(models);
    }

    @Override
    public SqlParams delete() {
        return this.queryAction.delete();
    }

    @Override
    public <M> SqlParams delete(M model) {
        return this.queryAction.delete(model);
    }

    @Override
    public SqlParams count() {
        return this.queryAction.count();
    }

    @Override
    public Query andEq(String column, Object value) {
        return this.queryConditionAnd.andEq(column, value);
    }

    @Override
    public Query andNotEq(String column, Object value) {
        return this.queryConditionAnd.andNotEq(column, value);
    }

    @Override
    public Query andGreat(String column, Object value) {
        return this.queryConditionAnd.andGreat(column, value);
    }

    @Override
    public Query andGreatEq(String column, Object value) {
        return this.queryConditionAnd.andGreatEq(column, value);
    }

    @Override
    public Query andLess(String column, Object value) {
        return this.queryConditionAnd.andLess(column, value);
    }

    @Override
    public Query andLessEq(String column, Object value) {
        return this.queryConditionAnd.andLessEq(column, value);
    }

    @Override
    public Query andLike(String column, String value) {
        return this.queryConditionAnd.andLike(column, value);
    }

    @Override
    public Query andNotLike(String column, String value) {
        return this.queryConditionAnd.andNotLike(column, value);
    }

    @Override
    public Query andIsNull(String column) {
        return this.queryConditionAnd.andIsNull(column);
    }

    @Override
    public Query andIsNotNull(String column) {
        return this.queryConditionAnd.andIsNotNull(column);
    }

    @Override
    public Query andIn(String column, Collection<?> value) {
        return this.queryConditionAnd.andIn(column, value);
    }

    @Override
    public Query andNotIn(String column, Collection<?> value) {
        return this.queryConditionAnd.andNotIn(column, value);
    }

    @Override
    public Query andBetween(String column, Object value1, Object value2) {
        return this.queryConditionAnd.andBetween(column, value1, value2);
    }

    @Override
    public Query andNotBetween(String column, Object value1, Object value2) {
        return this.queryConditionAnd.andNotBetween(column, value1, value2);
    }

    @Override
    public Query and(Query condition) {
        return this.queryConditionAnd.and(condition);
    }

    @Override
    public Query andEq(Column column, Object value) {
        return this.queryConditionAnd.andEq(column, value);
    }

    @Override
    public Query andNotEq(Column column, Object value) {
        return this.queryConditionAnd.andNotEq(column, value);
    }

    @Override
    public Query andGreat(Column column, Object value) {
        return this.queryConditionAnd.andGreat(column, value);
    }

    @Override
    public Query andGreatEq(Column column, Object value) {
        return this.queryConditionAnd.andGreatEq(column, value);
    }

    @Override
    public Query andLess(Column column, Object value) {
        return this.queryConditionAnd.andLess(column, value);
    }

    @Override
    public Query andLessEq(Column column, Object value) {
        return this.queryConditionAnd.andLessEq(column, value);
    }

    @Override
    public Query andLike(Column column, String value) {
        return this.queryConditionAnd.andLike(column, value);
    }

    @Override
    public Query andNotLike(Column column, String value) {
        return this.queryConditionAnd.andNotLike(column, value);
    }

    @Override
    public Query andIsNull(Column column) {
        return this.queryConditionAnd.andIsNull(column);
    }

    @Override
    public Query andIsNotNull(Column column) {
        return this.queryConditionAnd.andIsNotNull(column);
    }

    @Override
    public Query andIn(Column column, Collection<?> value) {
        return this.queryConditionAnd.andIn(column, value);
    }

    @Override
    public Query andNotIn(Column column, Collection<?> value) {
        return this.queryConditionAnd.andNotIn(column, value);
    }

    @Override
    public Query andBetween(Column column, Object value1, Object value2) {
        return this.queryConditionAnd.andBetween(column, value1, value2);
    }

    @Override
    public Query andNotBetween(Column column, Object value1, Object value2) {
        return this.queryConditionAnd.andNotBetween(column, value1, value2);
    }

    @Override
    public Query andSearchParamBuilder(SearchParamBuilder searchParamBuilder) {
        return this.queryConditionAnd.andSearchParamBuilder(searchParamBuilder);
    }

    @Override
    public Query orEq(String column, Object value) {
        return this.queryConditionOr.orEq(column, value);
    }

    @Override
    public Query orNotEq(String column, Object value) {
        return this.queryConditionOr.orNotEq(column, value);
    }

    @Override
    public Query orGreat(String column, Object value) {
        return this.queryConditionOr.orGreat(column, value);
    }

    @Override
    public Query orGreatEq(String column, Object value) {
        return this.queryConditionOr.orGreatEq(column, value);
    }

    @Override
    public Query orLess(String column, Object value) {
        return this.queryConditionOr.orLess(column, value);
    }

    @Override
    public Query orLessEq(String column, Object value) {
        return this.queryConditionOr.orLessEq(column, value);
    }

    @Override
    public Query orLike(String column, String value) {
        return this.queryConditionOr.orLike(column, value);
    }

    @Override
    public Query orNotLike(String column, String value) {
        return this.queryConditionOr.orNotLike(column, value);
    }

    @Override
    public Query orIsNull(String column) {
        return this.queryConditionOr.orIsNull(column);
    }

    @Override
    public Query orIsNotNull(String column) {
        return this.queryConditionOr.orIsNotNull(column);
    }

    @Override
    public Query orIn(String column, Collection<?> value) {
        return this.queryConditionOr.orIn(column, value);
    }

    @Override
    public Query orNotIn(String column, Collection<?> value) {
        return this.queryConditionOr.orNotIn(column, value);
    }

    @Override
    public Query orBetween(String column, Object value1, Object value2) {
        return this.queryConditionOr.orBetween(column, value1, value2);
    }

    @Override
    public Query orNotBetween(String column, Object value1, Object value2) {
        return this.queryConditionOr.orNotBetween(column, value1, value2);
    }

    @Override
    public Query or(Query condition) {
        return this.queryConditionOr.or(condition);
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

    @Override
    public Query whereOneEqualOne() {
        return this.queryConditionWhere.whereOneEqualOne();
    }

    @Override
    public Query whereEq(String column, Object value) {
        return this.queryConditionWhere.whereEq(column, value);
    }

    @Override
    public Query whereNotEq(String column, Object value) {
        return this.queryConditionWhere.whereNotEq(column, value);
    }

    @Override
    public Query whereGreat(String column, Object value) {
        return this.queryConditionWhere.whereGreat(column, value);
    }

    @Override
    public Query whereGreatEq(String column, Object value) {
        return this.queryConditionWhere.whereGreatEq(column, value);
    }

    @Override
    public Query whereLess(String column, Object value) {
        return this.queryConditionWhere.whereLess(column, value);
    }

    @Override
    public Query whereLessEq(String column, Object value) {
        return this.queryConditionWhere.whereLessEq(column, value);
    }

    @Override
    public Query whereLike(String column, String value) {
        return this.queryConditionWhere.whereLike(column, value);
    }

    @Override
    public Query whereNotLike(String column, String value) {
        return this.queryConditionWhere.whereNotLike(column, value);
    }

    @Override
    public Query whereIsNull(String column) {
        return this.queryConditionWhere.whereIsNull(column);
    }

    @Override
    public Query whereIsNotNull(String column) {
        return this.queryConditionWhere.whereIsNotNull(column);
    }

    @Override
    public Query whereIn(String column, Collection<?> value) {
        return this.queryConditionWhere.whereIn(column, value);
    }

    @Override
    public Query whereNotIn(String column, Collection<?> value) {
        return this.queryConditionWhere.whereNotIn(column, value);
    }

    @Override
    public Query whereBetween(String column, Object value1, Object value2) {
        return this.queryConditionWhere.whereBetween(column, value1, value2);
    }

    @Override
    public Query whereNotBetween(String column, Object value1, Object value2) {
        return this.queryConditionWhere.whereNotBetween(column, value1, value2);
    }

    @Override
    public Query where(Query condition) {
        return this.queryConditionWhere.where(condition);
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

    @Override
    public Query having(String condition) {
        return this.queryOther.having(condition);
    }

    @Override
    public Query groupBy(String column) {
        return this.queryOther.groupBy(column);
    }

    @Override
    public Query orderBy(String orderBy) {
        return this.queryOther.orderBy(orderBy);
    }

    @Override
    public Query asc(String column) {
        return this.queryOther.asc(column);
    }

    @Override
    public Query desc(String column) {
        return this.queryOther.desc(column);
    }

    @Override
    public Query limit(long startRow, long pageSize) {
        return this.queryOther.limit(startRow, pageSize);
    }

    @Override
    public Query join(Class<?> model) {
        return this.queryOther.join(model);
    }

    @Override
    public Query join(Class<?> model, String alias) {
        return this.queryOther.join(model, alias);
    }

    @Override
    public Query leftJoin(Class<?> model) {
        return this.queryOther.leftJoin(model);
    }

    @Override
    public Query leftJoin(Class<?> model, String alias) {
        return this.queryOther.leftJoin(model, alias);
    }

    @Override
    public Query on(String leftColumn, String rightColumn) {
        return this.queryOther.on(leftColumn, rightColumn);
    }
}
