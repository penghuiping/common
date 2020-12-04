package com.php25.common.db.cnd.sql;

import com.google.common.collect.Lists;
import com.php25.common.db.cnd.annotation.GeneratedValue;
import com.php25.common.db.cnd.annotation.SequenceGenerator;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/12/2 14:11
 */
public class OracleQuery extends BaseQuery {
    private static final Logger log = LoggerFactory.getLogger(OracleQuery.class);

    @Override
    protected <M> SqlParams insert(M model, boolean ignoreNull) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(model, ignoreNull);

        //获取主键名
        String id = JdbcModelManager.getPrimaryKeyColName(clazz);

        //是否是使用了sequence的情况
        boolean flag = false;

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
                    throw new DbException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw new DbException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JdbcModelManager.getAnnotationSequenceGenerator(clazz);
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

        if (flag) {
            //sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight());
                    } else {
                        stringBuilder.append("?");
                        params.add(paramConvert(pairList.get(i).getRight()));
                    }
                } else {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight()).append(",");
                    } else {
                        stringBuilder.append("?,");
                        params.add(paramConvert(pairList.get(i).getRight()));
                    }
                }

            }
        } else {
            //非sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("?");
                    params.add(paramConvert(pairList.get(i).getRight()));
                } else {
                    stringBuilder.append("?,");
                    params.add(paramConvert(pairList.get(i).getRight()));
                }

            }
        }

        stringBuilder.append(" )");
        String targetSql = stringBuilder.toString();
        log.info("sql语句为:{}", targetSql);
        SqlParams sqlParams = new SqlParams();
        sqlParams.setSql(targetSql);
        sqlParams.setParams(Lists.newCopyOnWriteArrayList(params));
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
            String result = String.format("SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (%s) A WHERE ROWNUM <= %s) WHERE RN >= %s", sb.toString(), pageSize, startRow);
            this.setSql(new StringBuilder(result));
        }
    }
}
