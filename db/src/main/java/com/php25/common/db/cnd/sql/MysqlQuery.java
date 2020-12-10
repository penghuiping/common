package com.php25.common.db.cnd.sql;

import com.google.common.collect.Lists;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.cnd.GenerationType;
import com.php25.common.db.cnd.annotation.GeneratedValue;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/12/2 14:11
 */
public class MysqlQuery extends BaseQuery {
    private static final Logger log = LoggerFactory.getLogger(MysqlQuery.class);

    public MysqlQuery(Class<?> model) {
        this.clazz = model;
    }

    public MysqlQuery(Class<?> model, String alias) {
        this(model);
        if (!StringUtil.isBlank(alias)) {
            aliasMap.put(alias, model);
            clazzAlias = alias;
        }
    }

    @Override
    protected <M> SqlParams insert(M model, boolean ignoreNull) {
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(model, ignoreNull);

        GenerationType generationType = GenerationType.AUTO;

        //判断主键属性上是否有@GeneratedValue注解
        Optional<GeneratedValue> generatedValueOptional = JdbcModelManager.getAnnotationGeneratedValue(clazz);
        if (generatedValueOptional.isPresent()) {
            //判断策略
            GeneratedValue generatedValue = generatedValueOptional.get();
            switch (generatedValue.strategy()) {
                case AUTO:
                    //程序指定,什么也不需要做
                    break;
                case TABLE:
                    throw new DbException("抱歉!mysql不支持这种模式");
                case IDENTITY:
                    generationType = GenerationType.IDENTITY;
                    //获取id column名
                    String id = JdbcModelManager.getPrimaryKeyColName(clazz);
                    //由于使用了mysql auto-increment 所以直接移除id
                    pairList = pairList.stream().filter(stringObjectImmutablePair -> !stringObjectImmutablePair.getLeft().equals(id)).collect(Collectors.toList());
                    break;
                case SEQUENCE:
                    throw new DbException("抱歉!mysql不支持这种模式");
                default:
                    //程序指定,什么也不需要做
                    break;
            }
        }

        //判断是否有@version注解
        Optional<Field> versionFieldOptional = JdbcModelManager.getVersionField(clazz);
        if (versionFieldOptional.isPresent()) {
            String versionColumnName = JdbcModelManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
            //不管version有没有值,由于是insert version的值默认都从0开始
            pairList = pairList.stream().filter(stringObjectImmutablePair -> !stringObjectImmutablePair.getLeft().equals(versionColumnName)).collect(Collectors.toList());
            pairList.add(new ImmutablePair<>(versionColumnName, 0));
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
            //添加参数
            params.add(paramConvert(pairList.get(i).getRight()));
        }
        stringBuilder.append(" )");
        String targetSql = stringBuilder.toString();
        log.info("sql语句为:{}", targetSql);

        SqlParams sqlParams = new SqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setParams(Lists.newCopyOnWriteArrayList(params));
        sqlParams.setClazz(this.clazz);
        sqlParams.setGenerationType(generationType);
        sqlParams.setModel(model);
        this.clear();
        return sqlParams;
    }

    @Override
    public SqlParams delete() {
        StringBuilder sb = new StringBuilder("DELETE");
        if (!StringUtil.isBlank(clazzAlias)) {
            //存在别名
            sb.append(" ").append(clazzAlias);
            sb.append(" FROM ").append(JdbcModelManager.getTableName(clazz)).append(" ").append(clazzAlias);
        } else {
            //不存在别名
            sb.append(" FROM ").append(JdbcModelManager.getTableName(clazz));
        }
        sb.append(" ").append(getSql());
        this.setSql(sb);
        String targetSql = this.getSql().toString();
        log.info("sql语句为:{}", targetSql);
        SqlParams sqlParams = new SqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(this.clazz);
        sqlParams.setParams(this.getParams());
        this.clear();
        return sqlParams;
    }

    @Override
    protected void addAdditionalPartSql() {
        StringBuilder sb = this.getSql();
        if (this.orderBy != null) {
            sb.append(orderBy.getOrderBy()).append(" ");
        }

        if (this.groupBy != null) {
            sb.append(groupBy.getGroupBy()).append(" ");
        }
        // 增加翻页
        if (this.startRow != -1) {
            sb.append(String.format("limit %s,%s", startRow, pageSize)).append(" ");
        }
    }
}
