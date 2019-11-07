package com.php25.common.db.cnd;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2019/7/25 15:19
 * @description:
 */
public abstract class CndJdbc extends AbstractNewQuery implements Query {

    private static final Logger log = LoggerFactory.getLogger(CndJdbc.class);

    JdbcOperations jdbcOperations = null;

    DbType dbType;

    public static CndJdbc of(Class cls, DbType dbType, JdbcOperations jdbcOperations) {
        CndJdbc dsl = null;
        switch (dbType) {
            case MYSQL:
                dsl = new CndMysqlJdbc(cls, jdbcOperations);
                break;
            case ORACLE:
                dsl = new CndOracleJdbc(cls, jdbcOperations);
                break;
            case POSTGRES:
                dsl = new CndPostgresJdbc(cls, jdbcOperations);
                break;
            default:
                dsl = new CndMysqlJdbc(cls, jdbcOperations);
                break;
        }
        return dsl;
    }

    public CndJdbc condition() {
        CndJdbc dsl = null;
        switch (dbType) {
            case MYSQL:
                dsl = new CndMysqlJdbc(this.clazz, this.jdbcOperations);
                break;
            case ORACLE:
                dsl = new CndOracleJdbc(this.clazz, this.jdbcOperations);
                break;
            case POSTGRES:
                dsl = new CndPostgresJdbc(this.clazz, jdbcOperations);
                break;
            default:
                dsl = new CndMysqlJdbc(this.clazz, jdbcOperations);
                break;
        }
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
            if (resultType.equals(clazz)) {
                sb = new StringBuilder("SELECT a.*");
            } else {
                sb = new StringBuilder("SELECT b.*");
            }
        }
        sb.append(" FROM ").append(JdbcModelManager.getTableName(clazz)).append(" a ").append(getSql());
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
            list = this.jdbcOperations.query(targetSql, paras, new JdbcModelRowMapper<T>(resultType));
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
    public <M> int[] insertBatch(List<M> list) {
        //泛型获取类所有的属性
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(list.get(0), false);

        //判断是否有@version注解
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        String versionColumnName = null;
        if (versionFieldOptional.isPresent()) {
            versionColumnName = JdbcModelManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
        }

        //拼装sql语句
        for (int i = 0; i < pairList.size(); i++) {
            if (i == (pairList.size() - 1)) {
                stringBuilder.append(pairList.get(i).getLeft());
            } else {
                stringBuilder.append(pairList.get(i).getLeft() + ",");
            }
        }
        stringBuilder.append(" ) VALUES ( ");
        for (int i = 0; i < pairList.size(); i++) {
            if (i == (pairList.size() - 1)) {
                stringBuilder.append("?");
            } else {
                stringBuilder.append("?,");
            }
        }
        stringBuilder.append(" )");
        log.info("sql语句为:" + stringBuilder.toString());

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            List<Object> params = new ArrayList<>();
            List<ImmutablePair<String, Object>> tmp = JdbcModelManager.getTableColumnNameAndValue(list.get(j), false);
            for (int i = 0; i < tmp.size(); i++) {
                //判断是否有@version注解，如果有默认给0
                if (versionFieldOptional.isPresent()) {
                    if (tmp.get(i).getLeft().equals(versionColumnName)) {
                        params.add(0);
                    } else {
                        params.add(paramConvert(tmp.get(i).getRight()));
                    }
                } else {
                    params.add(paramConvert(tmp.get(i).getRight()));
                }
            }
            batchParams.add(params.toArray());
        }

        try {
            return jdbcOperations.batchUpdate(stringBuilder.toString(), batchParams);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("插入操作失败", e);
        } finally {
            clear();
        }
    }

    @Override
    public <T> int insertIncludeNull(T t) {
        return insert(t, false);
    }

    @Override
    public int delete() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(JdbcModelManager.getTableName(clazz)).append(" a ").append(getSql());
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
        sb.append(JdbcModelManager.getTableName(clazz)).append(" a ").append(getSql());
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
    public CndJdbc having(QueryCondition condition) {
        // 去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }
        if (this.groupBy == null) {
            throw Exceptions.throwIllegalStateException("having 需要在groupBy后调用");
        }
        groupBy.addHaving(condition.getSql().toString());
        this.addParam(condition.getParams());
        return this;
    }

    @Override
    public CndJdbc groupBy(String column) {
        GroupBy groupBy = getGroupBy();
        groupBy.add(getCol(column));
        return this;
    }

    @Override
    public CndJdbc orderBy(String orderBy) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(orderBy);
        return this;
    }

    @Override
    public CndJdbc asc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderBy.add(getCol(column) + " ASC");
        return this;
    }

    @Override
    public CndJdbc desc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderBy.add(column + " DESC");
        return this;
    }

    @Override
    public CndJdbc join(Class<?> model, String column) {
        String tmp = getSql().toString();
        if (!StringUtil.isBlank(tmp) && tmp.contains("JOIN")) {
            throw Exceptions.throwIllegalStateException("join只能使用一次");
        }
        String joinStatement = "a." + JdbcModelManager.getDbColumnByClassColumn(clazz, column) + "=b." + JdbcModelManager.getPrimaryKeyColName(model);
        StringBuilder sb = new StringBuilder(String.format("JOIN %s b ON %s ", JdbcModelManager.getTableName(model), joinStatement));
        sb.append(getSql());
        this.setSql(sb);
        return this;
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public CndJdbc limit(long startRow, long pageSize) {
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

    /**
     * 实现sql语句中的insert
     *
     * @param t
     * @param ignoreNull 是否忽略 实体对象t中为null的属性项
     * @param <T>
     * @return
     */
    protected abstract <T> int insert(T t, boolean ignoreNull);


    private <T> int update(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("UPDATE " + JdbcModelManager.getTableName(t.getClass()) + " SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(t, ignoreNull);
        //获取主键id
        String pkName = JdbcModelManager.getPrimaryKeyColName(t.getClass());

        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        String versionColumnName = null;
        Long versionValue = 0L;

        Object pkValue = null;
        for (int i = 0; i < pairList.size(); i++) {
            //移除主键
            if (!pairList.get(i).getLeft().equals(pkName)) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append(pairList.get(i).getLeft()).append("=? ");
                } else {
                    stringBuilder.append(pairList.get(i).getLeft()).append("=?,");
                }
                if (versionFieldOptional.isPresent()) {
                    //@version注解字段，那么每次更新的时候都应该自动+1
                    //检查version属性是否是Long
                    Assert.isTrue(versionFieldOptional.get().getType().isAssignableFrom(Long.class), "version字段必须是Long类型");
                    versionColumnName = JdbcModelManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
                    if (pairList.get(i).getLeft().equals(versionColumnName)) {
                        versionValue = (Long) pairList.get(i).getRight();
                        params.add(versionValue + 1L);
                    } else {

                        params.add(paramConvert(pairList.get(i).getRight()));
                    }
                } else {
                    params.add(paramConvert(pairList.get(i).getRight()));
                }
            } else {
                pkValue = paramConvert(pairList.get(i).getRight());
            }
        }


        stringBuilder.append(String.format("WHERE %s=?", pkName));
        params.add(pkValue);

        if (versionFieldOptional.isPresent()) {
            //具有@version的情况
            stringBuilder.append(String.format(" AND %s=?", versionColumnName));
            params.add(versionValue);
        }

        log.info("sql语句为:" + stringBuilder.toString());
        try {
            return jdbcOperations.update(stringBuilder.toString(), params.toArray());
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("更新操作失败", e);
        } finally {
            clear();
        }
    }

    @Override
    public <T> int[] updateBatch(List<T> lists) {
        T t = lists.get(0);
        //泛型获取类所有的属性
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("UPDATE " + JdbcModelManager.getTableName(t.getClass()) + " SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(t, false);
        //获取主键id
        String pkName = JdbcModelManager.getPrimaryKeyColName(t.getClass());

        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        String versionColumnName = null;
        Long versionValue = 0L;

        //拼装sql
        for (int i = 0; i < pairList.size(); i++) {
            //移除主键
            if (!pairList.get(i).getLeft().equals(pkName)) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append(pairList.get(i).getLeft()).append("=? ");
                } else {
                    stringBuilder.append(pairList.get(i).getLeft()).append("=?,");
                }
            }
        }

        stringBuilder.append(String.format("WHERE %s=?", pkName));

        if (versionFieldOptional.isPresent()) {
            //具有@version的情况
            //检查version属性是否是Long
            Assert.isTrue(versionFieldOptional.get().getType().isAssignableFrom(Long.class), "version字段必须是Long类型");
            versionColumnName = JdbcModelManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
            stringBuilder.append(String.format(" AND %s=?", versionColumnName));
        }

        log.info("sql语句为:" + stringBuilder.toString());

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < lists.size(); j++) {
            Object pkValue = null;
            List<Object> params1 = new ArrayList<>();
            pairList = JdbcModelManager.getTableColumnNameAndValue(lists.get(j), false);
            for (int i = 0; i < pairList.size(); i++) {
                if (!pairList.get(i).getLeft().equals(pkName)) {
                    if (versionFieldOptional.isPresent()) {
                        //@version注解字段，那么每次更新的时候都应该自动+1
                        if (pairList.get(i).getLeft().equals(versionColumnName)) {
                            versionValue = (Long) pairList.get(i).getRight();
                            params1.add(versionValue + 1L);
                        } else {
                            params1.add(paramConvert(pairList.get(i).getRight()));
                        }
                    } else {
                        params1.add(paramConvert(pairList.get(i).getRight()));
                    }
                } else {
                    pkValue = paramConvert(pairList.get(i).getRight());
                }
            }
            params1.add(pkValue);
            params1.add(versionValue);
            batchParams.add(params1.toArray());
        }
        try {
            return jdbcOperations.batchUpdate(stringBuilder.toString(), batchParams);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("批量更新操作失败", e);
        } finally {
            clear();
        }
    }


    /**
     * 通过searchParamBuilder来构造查询条件
     *
     * @param searchParamBuilder
     * @return
     */
    public CndJdbc andSearchParamBuilder(SearchParamBuilder searchParamBuilder) {
        List<SearchParam> searchParams = searchParamBuilder.build();
        for (SearchParam searchParam : searchParams) {
            searchParam.getFieldName();
            String operator = searchParam.getOperator().name();
            if (null != operator) {
                if ("eq".equals(operator.toLowerCase())) {
                    this.andEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("ne".equals(operator.toLowerCase())) {
                    this.andNotEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("like".equals(operator.toLowerCase())) {
                    this.andLike(searchParam.getFieldName(), (String) searchParam.getValue());
                } else if ("gt".equals(operator.toLowerCase())) {
                    this.andGreat(searchParam.getFieldName(), searchParam.getValue());
                } else if ("lt".equals(operator.toLowerCase())) {
                    this.andLess(searchParam.getFieldName(), searchParam.getValue());
                } else if ("gte".equals(operator.toLowerCase())) {
                    this.andGreatEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("lte".equals(operator.toLowerCase())) {
                    this.andLessEq(searchParam.getFieldName(), searchParam.getValue());
                } else if ("in".equals(operator.toLowerCase())) {
                    this.andIn(searchParam.getFieldName(), (Collection<?>) searchParam.getValue());
                } else if ("nin".equals(operator.toLowerCase())) {
                    this.andNotIn(searchParam.getFieldName(), (Collection<?>) searchParam.getValue());
                } else {
                    this.andEq(searchParam.getFieldName(), searchParam.getValue());
                }
            }
        }
        return this;
    }

    /**
     * insert时候进行参数转化
     * <p>
     * 对于自定义类型class，需要获取这个class的primary key值
     *
     * @param paramValue 源参数值
     * @return 最终参数值
     */
    Object paramConvert(Object paramValue) {
        if (null == paramValue) {
            return null;
        }
        Class<?> paramValueType = paramValue.getClass();
        if (paramValueType.isPrimitive() || Number.class.isAssignableFrom(paramValueType) || String.class.isAssignableFrom(paramValueType)) {
            //基本类型,string,date直接加入参数列表
            return paramValue;
        }else if(Date.class.isAssignableFrom(paramValueType)) {
            Date tmp = (Date)paramValue;
            return new Timestamp(tmp.getTime());
        }else if(LocalDateTime.class.isAssignableFrom(paramValueType)) {
            LocalDateTime tmp = (LocalDateTime)paramValue;
            return new Timestamp(Date.from(tmp.toInstant(ZoneOffset.ofHours(8))).getTime());
        } else {
            if (!(Collection.class.isAssignableFrom(paramValueType))) {
                //自定义class类，通过反射获取主键值，在加入参数列表
                String subClassPk = JdbcModelManager.getPrimaryKeyFieldName(paramValueType);
                try {
                    return ReflectUtil.getMethod(paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)).invoke(paramValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw Exceptions.throwIllegalStateException(String.format("%s没有%s方法", paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)), e);
                }
            } else {
                //Collection类型不做任何处理
                throw Exceptions.throwIllegalStateException("此orm框架中model中不支持Collection类型的属性");
            }
        }
    }

    /**
     * 增加分页，排序
     */
    protected abstract void addAdditionalPartSql();
}