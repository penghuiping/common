package com.php25.common.db.core.sql;

import com.google.common.collect.Lists;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;
import com.php25.common.db.util.StringFormatter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author penghuiping
 * @date 2020/12/2 13:32
 */
public abstract class BaseQuery extends BaseQuery0 implements Query {
    private static final Logger log = LoggerFactory.getLogger(BaseQuery.class);

    @Override
    public SqlParams select(Class<?> model, String... columns) {
        AssertUtil.notNull(model, "model类型不能为null");
        StringBuilder sb = null;
        if (null != columns && columns.length > 0) {
            sb = new StringBuilder("SELECT ");
            for (String column : columns) {
                sb.append(getCol(column)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb = new StringBuilder("SELECT *");
        }
        sb.append(" FROM ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX);

        if (!StringUtil.isBlank(clazzAlias)) {
            sb.append(" ").append(clazzAlias);
        }
        sb.append(" ").append(getSql());
        this.setSql(sb);
        addAdditionalPartSql();
        String targetSql = this.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(this.clazz);
        sqlParams.setJoinClazz(this.joinClazz);
        sqlParams.setColumns(columns);
        sqlParams.setResultType(model);
        sqlParams.setParams(Lists.newCopyOnWriteArrayList(params));
        sqlParams.setStartRow((int) this.startRow);
        sqlParams.setPageSize((int) this.pageSize);
        sqlParams.setOrders(null != this.orderBy ? this.orderBy.getOrders() : null);
        this.clear();
        return sqlParams;

    }

    @Override
    public SqlParams select(String... columns) {
        return this.select(clazz, columns);
    }

    @Override
    public SqlParams single() {
        return select();
    }

    @Override
    public SqlParams select() {
        return this.select(clazz);
    }

    /**
     * 新增一条记录
     *
     * @param model      需要新增的实体类
     * @param ignoreNull 是否忽略实体对象中为null的属性项,true:忽略,false:不忽略
     * @return 返回sql语句
     */
    protected abstract <M> SqlParams insert(M model, boolean ignoreNull);

    /**
     * 更新一条记录
     *
     * @param model      需要新增的实体类
     * @param ignoreNull 是否忽略实体对象中为null的属性项,true:忽略,false:不忽略
     * @return 返回sql语句
     */
    private <M> SqlParams update(M model, boolean ignoreNull) {
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("UPDATE ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append(" SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(model, ignoreNull);
        //获取主键id
        String pkName = JdbcModelManager.getPrimaryKeyColName(model.getClass());
        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        String versionColumnName = null;
        Long versionValue = 0L;
        Object pkValue = null;
        boolean flag = false;
        List<Object> whereParams = params;
        String sql = this.getSql().toString();
        if (!StringUtil.isBlank(sql)) {
            flag = true;
            params = new ArrayList<>();
        }
        for (int i = 0; i < pairList.size(); i++) {
            //移除主键
            if (!pairList.get(i).getLeft().equals(pkName)) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("`").append(pairList.get(i).getLeft()).append("`").append("=? ");
                } else {
                    stringBuilder.append("`").append(pairList.get(i).getLeft()).append("`").append("=?,");
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
        if (flag) {
            params.addAll(whereParams);
            stringBuilder.append(sql);
        } else {
            stringBuilder.append(String.format("WHERE %s=?", pkName));
            params.add(pkValue);

            if (versionFieldOptional.isPresent() && null != versionColumnName) {
                //具有@version的情况
                stringBuilder.append(String.format(" AND %s=?", versionColumnName));
                params.add(versionValue);
            }
        }
        String targetSql = stringBuilder.toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setParams(this.params);
        sqlParams.setClazz(this.clazz);
        sqlParams.setModel(model);
        this.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams insert(M model) {
        return insert(model, true);
    }

    @Override
    public <M> SqlParams insertIncludeNull(M model) {
        return insert(model, false);
    }

    @Override
    public <M> SqlParams insertBatch(List<M> models) {
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(models.get(0), false);

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
                stringBuilder.append(pairList.get(i).getLeft()).append(",");
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
        String targetSql = stringBuilder.toString();

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < models.size(); j++) {
            List<Object> params = new ArrayList<>();
            List<ImmutablePair<String, Object>> tmp = JdbcModelManager.getTableColumnNameAndValue(models.get(j), false);
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
        BatchSqlParams sqlParams = new BatchSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setBatchParams(batchParams);
        sqlParams.setClazz(this.clazz);
        sqlParams.setModels((List<Object>) models);
        this.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams update(M model) {
        return update(model, true);
    }

    @Override
    public <M> SqlParams updateIncludeNull(M model) {
        return update(model, false);
    }

    @Override
    public <M> SqlParams updateBatch(List<M> models) {
        M model = models.get(0);
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("UPDATE ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append(" SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(model, false);
        //获取主键id
        String pkName = JdbcModelManager.getPrimaryKeyColName(model.getClass());

        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        String versionColumnName = null;
        Long versionValue = 0L;

        //拼装sql
        for (int i = 0; i < pairList.size(); i++) {
            //移除主键
            if (!pairList.get(i).getLeft().equals(pkName)) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("`").append(pairList.get(i).getLeft()).append("`").append("=? ");
                } else {
                    stringBuilder.append("`").append(pairList.get(i).getLeft()).append("`").append("=?,");
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

        String targetSql = stringBuilder.toString();

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < models.size(); j++) {
            Object pkValue = null;
            List<Object> params1 = new ArrayList<>();
            pairList = JdbcModelManager.getTableColumnNameAndValue(models.get(j), false);
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
            if (versionFieldOptional.isPresent()) {
                params1.add(versionValue);
            }
            batchParams.add(params1.toArray());
        }
        BatchSqlParams sqlParams = new BatchSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setBatchParams(batchParams);
        sqlParams.setClazz(this.clazz);
        sqlParams.setModels((List<Object>) models);
        this.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams delete(M model) {
        Object id = JdbcModelManager.getPrimaryKeyValue(clazz, model);
        String pkName = JdbcModelManager.getPrimaryKeyColName(clazz);
        return this.whereEq(pkName, id).delete();
    }

    @Override
    public SqlParams count() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(1) as num_count FROM ");
        sb.append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX);
        if (!StringUtil.isBlank(clazzAlias)) {
            sb.append(" ").append(clazzAlias);
        }
        sb.append(" ").append(getSql());
        this.setSql(sb);
        String targetSql = this.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(this.clazz);
        sqlParams.setJoinClazz(this.joinClazz);
        sqlParams.setParams(this.getParams());
        this.clear();
        return sqlParams;
    }

    /**
     * 增加分页，排序
     */
    protected abstract void addAdditionalPartSql();

    /**
     * insert时候进行参数转化
     * <p>
     * 对于自定义类型class，需要获取这个class的primary key值
     *
     * @param paramValue 源参数值
     * @return 最终参数值
     */
    protected Object paramConvert(Object paramValue) {
        if (null == paramValue) {
            return null;
        }
        Class<?> paramValueType = paramValue.getClass();
        if (paramValueType.isPrimitive() || Boolean.class.isAssignableFrom(paramValueType) || Number.class.isAssignableFrom(paramValueType) || String.class.isAssignableFrom(paramValueType)) {
            //基本类型,string,date直接加入参数列表
            return paramValue;
        } else if (Date.class.isAssignableFrom(paramValueType)) {
            Date tmp = (Date) paramValue;
            return new Timestamp(tmp.getTime());
        } else if (LocalDateTime.class.isAssignableFrom(paramValueType)) {
            LocalDateTime tmp = (LocalDateTime) paramValue;
            return new Timestamp(Date.from(tmp.toInstant(ZoneOffset.ofHours(8))).getTime());
        } else {
            if (!(Collection.class.isAssignableFrom(paramValueType))) {
                //自定义class类，通过反射获取主键值，在加入参数列表
                String subClassPk = JdbcModelManager.getPrimaryKeyFieldName(paramValueType);
                try {
                    return ReflectUtil.getMethod(paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)).invoke(paramValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DbException(String.format("%s没有%s方法", paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)), e);
                }
            } else {
                //Collection类型不做任何处理
                throw new DbException("此orm框架中model中不支持Collection类型的属性");
            }
        }
    }


    /**
     * 通过searchParamBuilder来构造查询条件
     *
     * @param searchParamBuilder
     * @return
     */
    public BaseQuery andSearchParamBuilder(SearchParamBuilder searchParamBuilder) {
        List<SearchParam> searchParams = searchParamBuilder.build();
        for (SearchParam searchParam : searchParams) {
            searchParam.getFieldName();
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
        return this;
    }
}
