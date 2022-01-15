package com.php25.common.db.core.sql;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.constant.Constants;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.mapper.GenerationType;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import com.php25.common.db.mapper.annotation.GeneratedValue;
import com.php25.common.db.mapper.annotation.SequenceGenerator;
import com.php25.common.db.util.StringFormatter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/12/2 14:11
 */
public class OracleQuery extends BaseQuery {
    private static final Logger log = LoggerFactory.getLogger(OracleQuery.class);

    public OracleQuery(Class<?> model) {
        this.queryContext.setClazz(model);
    }

    public OracleQuery(Class<?> model, String alias) {
        this(model);
        if (!StringUtil.isBlank(alias)) {
            this.queryContext.getAliasMap().put(alias, model);
            this.queryContext.setClazzAlias(alias);
        }
    }

    @Override
    public <M> SqlParams insert(M model, boolean ignoreNull) {
        Class<?> clazz = this.queryContext.getClazz();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelCacheManager.getTableColumnNameAndValue(model, ignoreNull);

        //获取主键名
        String id = JdbcModelCacheManager.getPrimaryKeyColName(clazz);

        GenerationType generationType = GenerationType.AUTO;

        //是否是使用了sequence的情况
        boolean flag = false;

        //判断主键属性上是否有@GeneratedValue注解
        Optional<GeneratedValue> generatedValueOptional = JdbcModelCacheManager.getAnnotationGeneratedValue(clazz);
        if (generatedValueOptional.isPresent()) {
            //判断策略
            GeneratedValue generatedValue = generatedValueOptional.get();
            switch (generatedValue.strategy()) {
                case AUTO:
                    //程序指定,什么也不需要做
                    break;
                case TABLE:
                    throw new DbException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw new DbException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    generationType = GenerationType.SEQUENCE;
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JdbcModelCacheManager.getAnnotationSequenceGenerator(clazz);
                    if (!sequenceGeneratorOptional.isPresent()) {
                        throw new DbException("@SequenceGenerator注解不存在");
                    } else {
                        SequenceGenerator sequenceGenerator = sequenceGeneratorOptional.get();
                        String sequenceName = sequenceGenerator.sequenceName();
                        Assert.hasText(sequenceName, "sequenceGenerator.sequenceName不能为空");

                        pairList = pairList.stream().map(pair -> {
                            if (pair.getLeft().equals(id)) {
                                //替换id的值为
                                return new ImmutablePair<String, Object>(pair.getLeft(), sequenceName + ".nextval");
                            } else {
                                return pair;
                            }
                        }).collect(Collectors.toList());
                    }
                    break;
                default:
                    //程序指定,什么也不需要做
                    break;
            }
        }

        //判断是否有@version注解
        Optional<Field> versionFieldOptional = JdbcModelCacheManager.getVersionField(clazz);
        if (versionFieldOptional.isPresent()) {
            String versionColumnName = JdbcModelCacheManager.getDbColumnByClassColumn(clazz, versionFieldOptional.get().getName());
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

        if (flag) {
            //sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight());
                    } else {
                        stringBuilder.append("?");
                        this.queryContext.getParams().add(AbstractQuery.paramConvert(pairList.get(i).getRight()));
                    }
                } else {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight()).append(",");
                    } else {
                        stringBuilder.append("?,");
                        this.queryContext.getParams().add(AbstractQuery.paramConvert(pairList.get(i).getRight()));
                    }
                }

            }
        } else {
            //非sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("?");
                    this.queryContext.getParams().add(AbstractQuery.paramConvert(pairList.get(i).getRight()));
                } else {
                    stringBuilder.append("?,");
                    this.queryContext.getParams().add(AbstractQuery.paramConvert(pairList.get(i).getRight()));
                }

            }
        }

        stringBuilder.append(" )");
        String targetSql = stringBuilder.toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setParams(this.queryContext.getParams());
        sqlParams.setClazz(clazz);
        sqlParams.setGenerationType(generationType);
        sqlParams.setModel(model);
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public <M> SqlParams insertBatch(List<M> models) {
        Class<?> clazz = this.queryContext.getClazz();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ")
                .append(StringFormatter.KEY_WRAPPER_PREFIX)
                .append(clazz.getSimpleName())
                .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                .append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelCacheManager.getTableColumnNameAndValue(models.get(0), false);

        //获取主键名
        String id = JdbcModelCacheManager.getPrimaryKeyColName(clazz);

        //是否是使用了sequence的情况
        boolean flag = false;

        //判断主键属性上是否有@GeneratedValue注解
        Optional<GeneratedValue> generatedValueOptional = JdbcModelCacheManager.getAnnotationGeneratedValue(clazz);
        if (generatedValueOptional.isPresent()) {
            //判断策略
            GeneratedValue generatedValue = generatedValueOptional.get();
            switch (generatedValue.strategy()) {
                case AUTO:
                    //程序指定,什么也不需要做
                    break;
                case TABLE:
                    throw new DbException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw new DbException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JdbcModelCacheManager.getAnnotationSequenceGenerator(clazz);
                    if (!sequenceGeneratorOptional.isPresent()) {
                        throw new DbException("@SequenceGenerator注解不存在");
                    } else {
                        SequenceGenerator sequenceGenerator = sequenceGeneratorOptional.get();
                        String sequenceName = sequenceGenerator.sequenceName();
                        Assert.hasText(sequenceName, "sequenceGenerator.sequenceName不能为空");

                        pairList = pairList.stream().map(pair -> {
                            if (pair.getLeft().equals(id)) {
                                //替换id的值为
                                return new ImmutablePair<String, Object>(pair.getLeft(), sequenceName + ".nextval");
                            } else {
                                return pair;
                            }
                        }).collect(Collectors.toList());
                    }
                    break;
                default:
                    //程序指定,什么也不需要做
                    break;
            }
        }


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
                stringBuilder.append(pairList.get(i).getLeft() + ",");
            }
        }
        stringBuilder.append(" ) VALUES ( ");


        if (flag) {
            //sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight());
                    } else {
                        stringBuilder.append("?");
                    }
                } else {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight()).append(",");
                    } else {
                        stringBuilder.append("?,");
                    }
                }
            }
        } else {
            //非sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("?");
                } else {
                    stringBuilder.append("?,");
                }

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

                if (flag) {
                    //sequence情况
                    //判断是否是id
                    if (tmp.get(i).getLeft().equals(id)) {
                        continue;
                    }
                }

                //判断是否有@version注解，如果有默认给0
                if (versionFieldOptional.isPresent()) {
                    if (tmp.get(i).getLeft().equals(versionColumnName)) {
                        params.add(0);
                    } else {
                        params.add(AbstractQuery.paramConvert(tmp.get(i).getRight()));
                    }
                } else {
                    params.add(AbstractQuery.paramConvert(tmp.get(i).getRight()));
                }
            }
            batchParams.add(params.toArray());
        }
        BatchSqlParams sqlParams = new BatchSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setBatchParams(batchParams);
        sqlParams.setClazz(clazz);
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public SqlParams delete() {
        String clazzAlias = this.queryContext.getClazzAlias();
        Class<?> clazz = this.queryContext.getClazz();
        StringBuilder sb = new StringBuilder("DELETE");
        if (!StringUtil.isBlank(clazzAlias)) {
            //存在别名
            sb.append(" FROM ")
                    .append(StringFormatter.KEY_WRAPPER_PREFIX)
                    .append(clazz.getSimpleName())
                    .append(StringFormatter.KEY_WRAPPER_SUFFIX)
                    .append(" ").append(clazzAlias);
        } else {
            //不存在别名
            sb.append(" FROM ")
                    .append(StringFormatter.KEY_WRAPPER_PREFIX)
                    .append(clazz.getSimpleName())
                    .append(StringFormatter.KEY_WRAPPER_SUFFIX);
        }
        sb.append(" ").append(getSql());
        this.queryContext.setSql(sb);
        String targetSql = this.getSql().toString();
        SingleSqlParams sqlParams = new SingleSqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setClazz(clazz);
        sqlParams.setParams(this.getParams());
        this.queryContext.clear();
        return sqlParams;
    }

    @Override
    public void addAdditionalPartSql() {
        StringBuilder sb = this.getSql();
        if (this.queryContext.getOrderBy() != null) {
            sb.append(this.queryContext.getOrderBy().getOrderBy()).append(" ");
        }

        if (this.queryContext.getGroupBy() != null) {
            sb.append(this.queryContext.getGroupBy().getGroupBy()).append(" ");
        }
        // 增加翻页
        if (this.queryContext.getStartRow() != -1) {
            String result = String.format("SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (%s) A WHERE ROWNUM <= (${%s}+${%s})) WHERE RN > ${%s}", sb.toString(), Constants.START_ROW, Constants.PAGE_SIZE, Constants.START_ROW);
            this.queryContext.setSql(new StringBuilder(result));
        }
    }
}
