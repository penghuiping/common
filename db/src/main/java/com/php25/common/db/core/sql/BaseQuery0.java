package com.php25.common.db.core.sql;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.GroupBy;
import com.php25.common.db.core.OrderBy;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.exception.DbException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author penghuiping
 * @date 2020/12/2 13:34
 */
public abstract class BaseQuery0 implements Query {
    public final String AND = "AND";
    public final String OR = "OR";
    public final String WHERE = "WHERE";
    public final String IN = "IN";
    public final String NOT_IN = "NOT IN";
    public final String BETWEEN = "BETWEEN";
    public final String NOT_BETWEEN = "NOT BETWEEN";
    protected StringBuilder sql = null;
    protected List<Object> params = new ArrayList<>();
    protected long startRow = -1, pageSize = -1;
    protected OrderBy orderBy = null;
    protected GroupBy groupBy = null;
    protected Class<?> clazz;
    protected List<Class<?>> joinClazz = new ArrayList<>();
    protected String clazzAlias;
    protected Map<String, Class<?>> aliasMap = new HashMap<>(8);


    protected void clear() {
        sql = null;
        params = new ArrayList<>();
        startRow = -1;
        pageSize = -1;
        orderBy = null;
        groupBy = null;
    }


    public String getCol(String name) {
        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            if (parts.length == 2) {
                //先尝试从aliasMap中获取
                Class<?> modelClass = aliasMap.getOrDefault(parts[0], null);
                if (null == modelClass) {
                    //不存在 没使用别名，试试是否是类名
                    modelClass = JdbcModelManager.getClassFromModelName(parts[0]);
                    if (modelClass == null) {
                        //不是，则使用原来的字符串
                        return " " + name + " ";
                    }
                    return getCol(modelClass, null, parts[1]);
                } else {
                    //存在说明使用了别名
                    return getCol(modelClass, parts[0], parts[1]);
                }
            } else {
                throw new DbException("Db Column name is illegal");
            }
        }
        return getCol(clazz, null, name);
    }

    protected String getCol(Class<?> modelClass, String alias, String name) {
        try {
            if (StringUtil.isBlank(alias)) {
                //没有使用别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" ${%s}.%s ", modelClass.getSimpleName(), JdbcModelManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" ${%s}.%s ", this.clazz.getSimpleName(), JdbcModelManager.getDbColumnByClassColumn(this.clazz, name));
                }
            } else {
                //使用了别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" %s.%s ", alias, JdbcModelManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" %s.%s ", alias, JdbcModelManager.getDbColumnByClassColumn(this.clazz, name));
                }
            }
        } catch (Exception e) {
            //"无法通过注解找到对应的column,直接使用传入的名字符串"
            return " " + name + " ";
        }
    }

    @Override
    public Query whereOneEqualOne() {
        this.getSql().append(" ").append(WHERE).append(" 1=1 ");
        return this;
    }


    @Override
    public Query whereEq(String column, Object value) {
        return andEq(column, value);
    }

    @Override
    public Query whereNotEq(String column, Object value) {
        return andNotEq(column, value);
    }

    @Override
    public Query whereGreat(String column, Object value) {
        return andGreat(column, value);
    }

    @Override
    public Query whereGreatEq(String column, Object value) {
        return andGreatEq(column, value);
    }

    @Override
    public Query whereLess(String column, Object value) {
        return andLess(column, value);
    }

    @Override
    public Query whereLessEq(String column, Object value) {
        return andLessEq(column, value);
    }

    @Override
    public Query whereLike(String column, String value) {
        return andLike(column, value);
    }

    @Override
    public Query whereNotLike(String column, String value) {
        return andNotLike(column, value);
    }

    @Override
    public Query whereIsNull(String column) {
        return andIsNull(column);
    }

    @Override
    public Query whereIsNotNull(String column) {
        return andIsNotNull(column);
    }

    @Override
    public Query whereIn(String column, Collection<?> value) {
        return andIn(column, value);
    }

    @Override
    public Query whereNotIn(String column, Collection<?> value) {
        return andNotIn(column, value);
    }

    @Override
    public Query whereBetween(String column, Object value1, Object value2) {
        return andBetween(column, value1, value2);
    }

    @Override
    public Query whereNotBetween(String column, Object value1, Object value2) {
        return andNotBetween(column, value1, value2);
    }

    /**
     * 拼接SQL
     *
     * @param sqlPart
     */
    protected BaseQuery0 appendSql(String sqlPart) {
        if (this.sql == null) {
            this.sql = new StringBuilder();
        }
        sql.append(sqlPart);
        return this;
    }


    /**
     * 增加参数
     *
     * @param objects
     * @return
     */
    protected BaseQuery0 addParam(Collection<?> objects) {
        params.addAll(objects);
        return this;
    }


    /**
     * 在头部增加参数
     *
     * @param objects
     * @return
     */
    protected BaseQuery0 addPreParam(List<Object> objects) {
        objects.addAll(params);
        params = objects;
        return this;
    }

    /**
     * 增加参数
     *
     * @param object
     * @return
     */
    protected BaseQuery0 addParam(Object object) {
        params.add(object);
        return this;
    }

    protected void appendAndSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, AND);
    }

    protected void appendOrSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, OR);
    }

    protected void appendSqlBase(String column, Object value, String opt, String link) {
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt);
        if (value != null) {
            this.appendSql(" ? ");
            this.addParam(value);
        }
    }

    protected void appendInSql(String column, Collection<?> value, String opt, String link) {
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ( ");
        for (Object o : value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        this.getSql().deleteCharAt(this.getSql().length() - 1);
        this.appendSql(" ) ");
    }

    protected void appendBetweenSql(String column, String opt, String link, Object... value) {
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }

        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ? AND ? ");
        this.addParam(value[0]);
        this.addParam(value[1]);
    }


    @Override
    public Query andEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return this;
    }

    @Override
    public Query andNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return this;
    }

    @Override
    public Query andGreat(String column, Object value) {
        appendAndSql(column, value, ">");
        return this;
    }

    @Override
    public Query andGreatEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return this;
    }

    @Override
    public Query andLess(String column, Object value) {
        appendAndSql(column, value, "<");
        return this;
    }

    @Override
    public Query andLessEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return this;
    }

    @Override
    public Query andLike(String column, String value) {
        appendAndSql(column, value, "LIKE ");
        return this;
    }

    @Override
    public Query andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE ");
        return this;
    }

    @Override
    public Query andIsNull(String column) {
        appendAndSql(column, null, "IS NULL ");
        return this;
    }

    @Override
    public Query andIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL ");
        return this;
    }

    @Override
    public Query andIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, AND);
        return this;
    }

    @Override
    public Query andNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return this;
    }

    @Override
    public Query andBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, AND, value1, value2);
        return this;
    }

    @Override
    public Query andNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, AND, value1, value2);
        return this;
    }

    @Override
    public Query orEq(String column, Object value) {
        appendOrSql(column, value, "=");
        return this;
    }

    @Override
    public Query orNotEq(String column, Object value) {
        appendOrSql(column, value, "<>");
        return this;
    }

    @Override
    public Query orGreat(String column, Object value) {
        appendOrSql(column, value, ">");
        return this;
    }

    @Override
    public Query orGreatEq(String column, Object value) {
        appendOrSql(column, value, ">=");
        return this;
    }

    @Override
    public Query orLess(String column, Object value) {
        appendOrSql(column, value, "<");
        return this;
    }

    @Override
    public Query orLessEq(String column, Object value) {
        appendOrSql(column, value, "<=");
        return this;
    }

    @Override
    public Query orLike(String column, String value) {
        appendOrSql(column, value, "LIKE");
        return this;
    }

    @Override
    public Query orNotLike(String column, String value) {
        appendOrSql(column, value, "NOT LIKE");
        return this;
    }

    @Override
    public Query orIsNull(String column) {
        appendOrSql(column, null, "IS NULL");
        return this;
    }

    @Override
    public Query orIsNotNull(String column) {
        appendOrSql(column, null, "IS NOT NULL");
        return this;
    }

    @Override
    public Query orIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, OR);
        return this;
    }

    @Override
    public Query orNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, OR);
        return this;
    }

    @Override
    public Query orBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, OR, value1, value2);
        return this;
    }

    @Override
    public Query orNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, OR, value1, value2);
        return this;
    }


    @Override
    public Query where(Query condition) {
        return manyCondition(condition, WHERE);
    }

    @Override
    public Query and(Query condition) {
        return manyCondition(condition, AND);
    }

    @Override
    public Query or(Query condition) {
        return manyCondition(condition, OR);
    }

    private Query manyCondition(Query condition, String link) {
        if (condition == null) {
            throw new DbException("连接条件必须是一个 QueryCondition 类型");
        }

        //去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }

        appendSql(link)
                .appendSql(" (")
                .appendSql(condition.getSql().toString())
                .appendSql(")");
        addParam(condition.getParams());
        return this;
    }

    @Override
    public StringBuilder getSql() {
        if (this.sql == null) {
            return new StringBuilder();
        }
        return this.sql;
    }

    protected void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

    @Override
    public BaseQuery0 having(String condition) {
        if (this.groupBy == null) {
            throw new DbException("having 需要在groupBy后调用");
        }
        groupBy.addHaving(condition);
        return this;
    }

    @Override
    public BaseQuery0 groupBy(String column) {
        GroupBy groupBy = getGroupBy();
        groupBy.add(getCol(column));
        return this;
    }

    @Override
    public BaseQuery0 orderBy(String orderBy) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(orderBy);
        return this;
    }

    @Override
    public BaseQuery0 asc(String column) {
        this.getOrderBy();
        orderBy.add(getCol(column) + " ASC");
        return this;
    }

    @Override
    public BaseQuery0 desc(String column) {
        this.getOrderBy();
        orderBy.add(getCol(column) + " DESC");
        return this;
    }


    @Override
    public BaseQuery0 join(Class<?> model) {
        return join(model, null);
    }

    @Override
    public BaseQuery0 join(Class<?> model, String alias) {
        this.setSql(this.getSql().append(String.format("JOIN ${%s}  ", model.getSimpleName())));

        if (!StringUtil.isBlank(alias)) {
            this.setSql(this.getSql().append(alias).append(" "));
            aliasMap.put(alias, model);
        }

        this.joinClazz.add(model);
        return this;
    }

    @Override
    public BaseQuery0 leftJoin(Class<?> model) {
        return leftJoin(model, null);
    }

    @Override
    public BaseQuery0 leftJoin(Class<?> model, String alias) {
        this.setSql(this.getSql().append(String.format("LEFT JOIN ${%s}  ", model.getSimpleName())));

        if (!StringUtil.isBlank(alias)) {
            this.setSql(this.getSql().append(alias).append(" "));
            aliasMap.put(alias, model);
        }

        this.joinClazz.add(model);
        return this;
    }

    @Override
    public BaseQuery0 on(String leftColumn, String rightColumn) {
        String left = getCol(leftColumn);
        String right = getCol(rightColumn);
        this.setSql(this.getSql().append(String.format("ON %s=%s", left, right)));
        return this;
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public BaseQuery0 limit(long startRow, long pageSize) {
        this.startRow = startRow;
        this.pageSize = pageSize;
        return this;
    }

    private OrderBy getOrderBy() {
        if (this.orderBy == null) {
            orderBy = new OrderBy();
        }
        return this.orderBy;
    }

    private GroupBy getGroupBy() {
        if (this.groupBy == null) {
            groupBy = new GroupBy();
        }
        return this.groupBy;
    }
}
