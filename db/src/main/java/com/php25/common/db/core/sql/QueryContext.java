package com.php25.common.db.core.sql;

import com.php25.common.db.core.GroupBy;
import com.php25.common.db.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/12/26 16:27
 */
public class QueryContext {

    private final Query query;
    private StringBuilder sql = null;
    private List<Object> params = new ArrayList<>();
    private Class<?> clazz;
    private String clazzAlias;
    private Map<String, Class<?>> aliasMap = new HashMap<>(8);
    private long startRow = -1, pageSize = -1;
    private OrderBy orderBy = null;
    private GroupBy groupBy = null;
    private List<Class<?>> joinClazz = new ArrayList<>();

    public QueryContext(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    protected void clear() {
        sql = null;
        params = new ArrayList<>();
        startRow = -1;
        pageSize = -1;
        orderBy = null;
        groupBy = null;
    }


    public StringBuilder getSql() {
        if (this.sql == null) {
            return new StringBuilder();
        }
        return this.sql;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClazzAlias() {
        return clazzAlias;
    }

    public void setClazzAlias(String clazzAlias) {
        this.clazzAlias = clazzAlias;
    }

    public Map<String, Class<?>> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(Map<String, Class<?>> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public long getStartRow() {
        return startRow;
    }

    public void setStartRow(long startRow) {
        this.startRow = startRow;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public List<Class<?>> getJoinClazz() {
        return joinClazz;
    }

    public void setJoinClazz(List<Class<?>> joinClazz) {
        this.joinClazz = joinClazz;
    }
}
