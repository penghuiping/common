package com.php25.common.db.core.sql;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.GroupBy;
import com.php25.common.db.core.OrderBy;
import com.php25.common.db.exception.DbException;


/**
 * @author penghuiping
 * @date 2020/12/2 13:34
 */
public class QueryOtherImpl extends AbstractQuery implements QueryOther {

    public QueryOtherImpl(QueryContext queryContext) {
        super(queryContext);
    }

    @Override
    public Query having(String condition) {
        if (this.queryContext.getGroupBy() == null) {
            throw new DbException("having 需要在groupBy后调用");
        }
        this.queryContext.getGroupBy().addHaving(condition);
        return this.queryContext.getQuery();
    }

    @Override
    public Query groupBy(String column) {
        GroupBy groupBy = getGroupBy();
        groupBy.add(getCol(column));
        return this.queryContext.getQuery();
    }

    @Override
    public Query orderBy(String orderBy) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(orderBy);
        return this.queryContext.getQuery();
    }

    @Override
    public Query asc(String column) {
        this.getOrderBy();
        this.queryContext.getOrderBy().add(getCol(column) + " ASC");
        return this.queryContext.getQuery();
    }

    @Override
    public Query desc(String column) {
        this.getOrderBy();
        this.queryContext.getOrderBy().add(getCol(column) + " DESC");
        return this.queryContext.getQuery();
    }


    @Override
    public Query join(Class<?> model) {
        return join(model, null);
    }

    @Override
    public Query join(Class<?> model, String alias) {
        this.queryContext.setSql(this.getSql().append(String.format("JOIN ${%s}  ", model.getSimpleName())));

        if (!StringUtil.isBlank(alias)) {
            this.queryContext.setSql(this.getSql().append(alias).append(" "));
            this.queryContext.getAliasMap().put(alias, model);
        }
        this.queryContext.getJoinClazz().add(model);
        return this.queryContext.getQuery();
    }

    @Override
    public Query leftJoin(Class<?> model) {
        return leftJoin(model, null);
    }

    @Override
    public Query leftJoin(Class<?> model, String alias) {
        this.queryContext.setSql(this.queryContext.getSql().append(String.format("LEFT JOIN ${%s}  ", model.getSimpleName())));

        if (!StringUtil.isBlank(alias)) {
            this.queryContext.setSql(this.queryContext.getSql().append(alias).append(" "));
            this.queryContext.getAliasMap().put(alias, model);
        }

        this.queryContext.getJoinClazz().add(model);
        return this.queryContext.getQuery();
    }

    @Override
    public Query on(String leftColumn, String rightColumn) {
        String left = getCol(leftColumn);
        String right = getCol(rightColumn);
        this.queryContext.setSql(this.queryContext.getSql().append(String.format("ON %s=%s", left, right)));
        return this.queryContext.getQuery();
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public Query limit(long startRow, long pageSize) {
        this.queryContext.setStartRow(startRow);
        this.queryContext.setPageSize(pageSize);
        return this.queryContext.getQuery();
    }

    private OrderBy getOrderBy() {
        if (this.queryContext.getOrderBy() == null) {
            this.queryContext.setOrderBy(new OrderBy());
        }
        return this.queryContext.getOrderBy();
    }

    private GroupBy getGroupBy() {
        if (this.queryContext.getGroupBy() == null) {
            this.queryContext.setGroupBy(new GroupBy());
        }
        return this.queryContext.getGroupBy();
    }
}
