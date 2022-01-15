package com.php25.common.db.core.sql;

import com.google.common.collect.Lists;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.OrderBy;
import com.php25.common.db.core.sql.column.AsColumn;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import com.php25.common.db.util.StringFormatter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author penghuiping
 * @date 2021/12/26 16:12
 */
public class QueryActionImpl extends AbstractQuery implements QueryAction {

    public QueryActionImpl(QueryContext queryContext) {
        super(queryContext);
    }

    @Override
    public SqlParams select(Class<?> model, AsColumn... columns) {
        AssertUtil.notNull(model, "model类型不能为null");
        StringBuilder sb = null;
        Class<?> clazz = this.queryContext.getClazz();
        String clazzAlias = this.queryContext.getClazzAlias();
        List<Class<?>> joinClazz = this.queryContext.getJoinClazz();
        List<Object> params = this.queryContext.getParams();
        long startRow = this.queryContext.getStartRow();
        long pageSize = this.queryContext.getPageSize();
        OrderBy orderBy = this.queryContext.getOrderBy();
        String[] columns0 = null;
        if (null != columns && columns.length > 0) {
            columns0 = new String[columns.length];
            sb = new StringBuilder("SELECT ");

            for (int i = 0; i < columns.length; i++) {
                AsColumn column = columns[i];
                columns0[i] = column.toString();
                sb.append(getCol(column.toString())).append(",");
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
        sb.append(" ").append(this.queryContext.getSql());
        this.queryContext.setSql(sb);
        this.queryContext.getQuery().addAdditionalPartSql();
        String targetSql = this.queryContext.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(clazz);
        sqlParams.setJoinClazz(joinClazz);
        sqlParams.setColumns(columns0);
        sqlParams.setResultType(model);
        sqlParams.setParams(Lists.newCopyOnWriteArrayList(params));
        sqlParams.setStartRow((int) startRow);
        sqlParams.setPageSize((int) pageSize);
        sqlParams.setOrders(null != orderBy ? orderBy.getOrders() : null);
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public SqlParams delete() {
        return null;
    }

    @Override
    public SqlParams select(AsColumn... columns) {
        return this.select(this.queryContext.getClazz(), columns);
    }

    @Override
    public SqlParams single() {
        return select();
    }

    @Override
    public SqlParams select() {
        return this.select(this.queryContext.getClazz());
    }


    /**
     * 更新一条记录
     *
     * @param model      需要新增的实体类
     * @param ignoreNull 是否忽略实体对象中为null的属性项,true:忽略,false:不忽略
     * @return 返回sql语句
     */
    private <M> SqlParams update(M model, boolean ignoreNull) {
        Class<?> clazz = this.queryContext.getClazz();
        List<Object> params = this.queryContext.getParams();
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("UPDATE ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(this.queryContext.getClazz().getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append(" SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelCacheManager.getTableColumnNameAndValue(model, ignoreNull);
        //获取主键id
        String pkName = JdbcModelCacheManager.getPrimaryKeyColName(model.getClass());
        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelCacheManager.getVersionField(this.queryContext.getClazz());
        String versionColumnName = null;
        Long versionValue = 0L;
        Object pkValue = null;
        boolean flag = false;
        List<Object> whereParams = params;
        String sql = this.queryContext.getSql().toString();
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
                    versionColumnName = JdbcModelCacheManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
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
        sqlParams.setParams(params);
        sqlParams.setClazz(clazz);
        sqlParams.setModel(model);
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams insert(M model) {
        return this.queryContext.getQuery().insert(model, true);
    }

    @Override
    public <M> SqlParams insertIncludeNull(M model) {
        return this.queryContext.getQuery().insert(model, false);
    }

    @Override
    public <M> SqlParams insertBatch(List<M> models) {
        Class<?> clazz = this.queryContext.getClazz();
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelCacheManager.getTableColumnNameAndValue(models.get(0), false);

        //判断是否有@version注解
        Optional<Field> versionFieldOptional = JdbcModelCacheManager.getVersionField(clazz);
        String versionColumnName = null;
        if (versionFieldOptional.isPresent()) {
            versionColumnName = JdbcModelCacheManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
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
            List<ImmutablePair<String, Object>> tmp = JdbcModelCacheManager.getTableColumnNameAndValue(models.get(j), false);
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
        sqlParams.setClazz(clazz);
        sqlParams.setModels((List<Object>) models);
        this.queryContext.clear();
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
        Class<?> clazz = this.queryContext.getClazz();
        M model = models.get(0);
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("UPDATE ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append(" SET ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelCacheManager.getTableColumnNameAndValue(model, false);
        //获取主键id
        String pkName = JdbcModelCacheManager.getPrimaryKeyColName(model.getClass());

        //判断是否有@version注解，如果有的话当然是进行乐观锁处理逻辑
        Optional<Field> versionFieldOptional = JdbcModelCacheManager.getVersionField(clazz);
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
            versionColumnName = JdbcModelCacheManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
            stringBuilder.append(String.format(" AND %s=?", versionColumnName));
        }

        String targetSql = stringBuilder.toString();

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < models.size(); j++) {
            Object pkValue = null;
            List<Object> params1 = new ArrayList<>();
            pairList = JdbcModelCacheManager.getTableColumnNameAndValue(models.get(j), false);
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
        sqlParams.setClazz(clazz);
        sqlParams.setModels((List<Object>) models);
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams delete(M model) {
        Class<?> clazz = this.queryContext.getClazz();
        Object id = JdbcModelCacheManager.getPrimaryKeyValue(clazz, model);
        String pkName = JdbcModelCacheManager.getPrimaryKeyColName(clazz);
        appendAndSql(pkName, id, "=");
        return this.delete();
    }

    @Override
    public SqlParams count() {
        Class<?> clazz = this.queryContext.getClazz();
        String clazzAlias = this.queryContext.getClazzAlias();
        List<Class<?>> joinClazz = this.queryContext.getJoinClazz();
        StringBuilder sb = new StringBuilder("SELECT COUNT(1) as num_count FROM ");
        sb.append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX);
        if (!StringUtil.isBlank(clazzAlias)) {
            sb.append(" ").append(clazzAlias);
        }
        sb.append(" ").append(this.queryContext.getSql());
        this.queryContext.setSql(sb);
        String targetSql = this.queryContext.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(clazz);
        sqlParams.setJoinClazz(joinClazz);
        sqlParams.setParams(this.queryContext.getParams());
        this.queryContext.clear();
        return sqlParams;
    }
}
