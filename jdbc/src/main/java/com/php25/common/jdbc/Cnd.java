package com.php25.common.jdbc;

import com.php25.common.core.util.StringUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/12 22:57
 * @Description:
 */
public class Cnd extends AbstractQuery implements Query {

    private static final Logger log = LoggerFactory.getLogger(Cnd.class);

    private JdbcOperations jdbcOperations = null;

    private Class clazz;

    private Cnd() {

    }

    protected static Cnd of(Class cls, JdbcOperations jdbcOperations) {
        Cnd dsl = new Cnd();
        dsl.jdbcOperations = jdbcOperations;
        dsl.clazz = cls;
        return dsl;
    }

    public Cnd condition() {
        Cnd dsl = new Cnd();
        dsl.jdbcOperations = this.jdbcOperations;
        dsl.clazz = this.clazz;
        return dsl;
    }

    @Override
    public <T> List<T> select(Class resultType, String... columns) {
        StringBuilder sb = null;
        if (null != columns && columns.length > 0) {
            sb = new StringBuilder("SELECT ");
            for (String column : columns) {
                sb.append(column).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb = new StringBuilder("SELECT *");
        }
        sb.append(" FROM ").append(ModelManager.getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);
        addAdditionalPartSql();
        String targetSql = this.getSql().toString();
        log.info("sql语句为:" + targetSql);
        Object[] paras = getParams().toArray();
        //先清楚
        clear();
        List<T> list = null;
        if (resultType.isAssignableFrom(Map.class)) {
            list = (List<T>) this.jdbcOperations.query(targetSql, paras, new ColumnMapRowMapper());
        } else {
            list = this.jdbcOperations.query(targetSql, paras, new BeanPropertyRowMapper<T>(resultType));
        }
        return list;
    }

    @Override
    public <T> List<T> select(String... columns) {
        return this.select(clazz, columns);
    }

    @Override
    public <T> List<T> select() {
        return this.select(clazz);
    }

    @Override
    public <T> T single() {
        List<T> list = limit(0, 1).select();
        if (list.isEmpty()) {
            return null;
        }
        // 同SQLManager.single 一致，只取第一条。
        return list.get(0);
    }

    @Override
    public Map mapSingle() {
        List<Map> list = limit(0, 1).select(Map.class);
        if (list.isEmpty()) {
            return null;
        }
        // 同SQLManager.single 一致，只取第一条
        return list.get(0);
    }

    @Override
    public List<Map> mapSelect() {
        return this.select(Map.class);
    }

    @Override
    public List<Map> mapSelect(String... columns) {
        return this.select(Map.class, columns);
    }

    @Override
    public <T> int update(T t) {
        return update(t, true);
    }

    @Override
    public <T> int updateIncludeNull(T t) {
        return update(t, false);
    }

    @Override
    public <T> int insert(T t) {
        return insert(t, true);
    }

    @Override
    public <T> int insertIncludeNull(T t) {
        return insert(t, false);
    }

    @Override
    public int delete() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(ModelManager.getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);
        log.info("sql语句为:" + sb.toString());
        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        int row = this.jdbcOperations.update(targetSql, paras);
        return row;
    }

    @Override
    public long count() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(1) FROM ");
        sb.append(ModelManager.getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);
        log.info("sql语句为:" + sb.toString());
        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        Long result = this.jdbcOperations.queryForObject(targetSql, Long.class, paras);
        return result;
    }

    @Override
    public Cnd having(QueryCondition condition) {
        // 去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }
        if (this.groupBy == null) {
            throw new RuntimeException("having 需要在groupBy后调用");
        }
        groupBy.addHaving(condition.getSql().toString());
        this.addParam(condition.getParams());
        return this;
    }

    @Override
    public Cnd groupBy(String column) {
        GroupBy groupBy = getGroupBy();
        groupBy.add(getCol(column));
        return this;
    }

    @Override
    public Cnd orderBy(String orderBy) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(orderBy);
        return this;
    }

    @Override
    public Cnd asc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderBy.add(getCol(column) + " ASC");
        return this;
    }

    @Override
    public Cnd desc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderBy.add(column + " DESC");
        return this;
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public Cnd limit(long startRow, long pageSize) {
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


    /***
     * 获取错误提示
     *
     * @return
     */
    private String getSqlErrorTip(String couse) {
        return String.format("\n┏━━━━━ SQL语法错误:\n" + "┣SQL：%s\n" + "┣原因：%s\n" + "┣解决办法：您可能需要重新获取一个Query\n" + "┗━━━━━\n",
                getSql().toString(), couse);
    }

    private <T> int insert(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + ModelManager.getTableName(t.getClass()) + "( ");
        List<ImmutablePair<String, Object>> pairList = ModelManager.getTableColumnNameAndValue(t, ignoreNull);
        //拼装sql语句
        for (int i = 0; i < pairList.size(); i++) {
            if (i == (pairList.size() - 1))
                stringBuilder.append(pairList.get(i).getLeft());
            else
                stringBuilder.append(pairList.get(i).getLeft() + ",");
        }
        stringBuilder.append(" ) VALUES ( ");
        for (int i = 0; i < pairList.size(); i++) {
            if (i == (pairList.size() - 1))
                stringBuilder.append("?");
            else
                stringBuilder.append("?,");
            params.add(pairList.get(i).getRight());
        }
        stringBuilder.append(" )");
        log.info("sql语句为:" + stringBuilder.toString());
        try {
            return jdbcOperations.update(stringBuilder.toString(), params.toArray());
        } catch (Exception e) {
            log.error("插入操作失败", e);
            throw new RuntimeException("插入操作失败", e);
        } finally {
            clear();
        }

    }

    private <T> int update(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("UPDATE " + ModelManager.getTableName(t.getClass()) + " SET ");
        List<ImmutablePair<String, Object>> pairList = ModelManager.getTableColumnNameAndValue(t, ignoreNull);
        //获取主键id
        Field pk = ModelManager.getPrimaryKeyColName(t);
        Assert.notNull(pk, "主键竟然为null?这不合理。");
        String pkName = pk.getName();
        Column column = pk.getAnnotation(Column.class);
        if (null != column && !StringUtil.isBlank(column.name())) pkName = column.name();

        Object pkValue = null;
        for (int i = 0; i < pairList.size(); i++) {
            //移除主键
            if (!pairList.get(i).getLeft().equals(pkName)) {
                if (i == (pairList.size() - 1))
                    stringBuilder.append(pairList.get(i).getLeft()).append("=? ");
                else
                    stringBuilder.append(pairList.get(i).getLeft()).append("=?,");
                params.add(pairList.get(i).getRight());
            } else {
                pkValue = pairList.get(i).getValue();
            }
        }
        stringBuilder.append(String.format("WHERE %s=?", pkName));
        params.add(pkValue);
        log.info("sql语句为:" + stringBuilder.toString());
        try {
            return jdbcOperations.update(stringBuilder.toString(), params.toArray());
        } catch (Exception e) {
            log.error("更新操作失败", e);
            throw new RuntimeException("更新操作失败", e);
        } finally {
            clear();
        }
    }

    /**
     * 增加分页，排序
     */
    private void addAdditionalPartSql() {
        StringBuilder sb = this.getSql();
        if (this.orderBy != null) {
            sb.append(orderBy.getOrderBy()).append(" ");
        }

        if (this.groupBy != null) {
            sb.append(groupBy.getGroupBy()).append(" ");
        }
        // 增加翻页
        if (this.startRow != -1) {
            //setSql(new StringBuilder(sqlManager.getDbStyle().getPageSQLStatement(this.getSql().toString(), startRow, pageSize)));
            //todo 只考虑了mysql
            sb.append(String.format("limit %s,%s", startRow, pageSize)).append(" ");
        }
    }
}
