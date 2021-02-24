package com.php25.common.db.core.sql;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.Constants;
import com.php25.common.db.core.GenerationType;
import com.php25.common.db.core.annotation.GeneratedValue;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.util.StringFormatter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/12/2 14:12
 */
public class SqliteQuery extends BaseQuery {
    private static final Logger log = LoggerFactory.getLogger(SqliteQuery.class);

    public SqliteQuery(Class<?> model) {
        this.clazz = model;
    }

    public SqliteQuery(Class<?> model, String alias) {
        this(model);
        if (!StringUtil.isBlank(alias)) {
            aliasMap.put(alias, model);
            clazzAlias = alias;
        }
    }

    @Override
    protected <M> SqlParams insert(M model, boolean ignoreNull) {
        //泛型获取类所有的属性
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(model, ignoreNull);

        //获取主键名
        String id = JdbcModelManager.getPrimaryKeyColName(clazz);

        //是否是使用了sequence的情况
        boolean flag = false;

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
                    throw new DbException("抱歉!sqlite不支持这种模式");
                case IDENTITY:
                    generationType = GenerationType.IDENTITY;
                    //由于使用了sqlite auto-increment 所以直接移除id
                    pairList = pairList.stream().filter(stringObjectImmutablePair -> !stringObjectImmutablePair.getLeft().equals(id)).collect(Collectors.toList());
                    break;
                case SEQUENCE:
                    throw new DbException("抱歉!sqlite不支持这种模式");
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

        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setParams(params);
        sqlParams.setSql(targetSql);
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
            throw new DbException("sqlite数据库delete操作不支持别名");
        } else {
            //不存在别名
            sb.append(" FROM ")
                    .append(StringFormatter.KEY_WRAPPER_PREFIX)
                    .append(clazz.getSimpleName())
                    .append(StringFormatter.KEY_WRAPPER_SUFFIX);
        }
        sb.append(" ").append(getSql());
        this.setSql(sb);
        String targetSql = this.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
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
            sb.append(String.format("limit ${%s} offset ${%s}", Constants.PAGE_SIZE, Constants.START_ROW)).append(" ");
        }
    }
}
