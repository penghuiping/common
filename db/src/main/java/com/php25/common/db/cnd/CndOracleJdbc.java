package com.php25.common.db.cnd;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2019/7/25 15:14
 * @description:
 */
public class CndOracleJdbc extends CndJdbc {

    private static final Logger log = LoggerFactory.getLogger(CndOracleJdbc.class);

    protected CndOracleJdbc(Class cls, JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.clazz = cls;
        this.dbType = DbType.ORACLE;
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

    private <T> ImmutablePair<StringBuilder, Boolean> getInsertSQL(T t, Boolean ignoreNull) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(t, ignoreNull);

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
                    throw Exceptions.throwIllegalStateException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw Exceptions.throwIllegalStateException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JdbcModelManager.getAnnotationSequenceGenerator(clazz);
                    if (!sequenceGeneratorOptional.isPresent()) {
                        throw Exceptions.throwIllegalStateException("@SequenceGenerator注解不存在");
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
        log.info("sql语句为:" + stringBuilder.toString());

        return new ImmutablePair<>(stringBuilder, flag);
    }


    @Override
    protected <T> int insert(T t, boolean ignoreNull) {
        ImmutablePair<StringBuilder, Boolean> pair = getInsertSQL(t, ignoreNull);
        try {
            if (pair.right) {
                //sequence情况
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);

                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = jdbcOperations.update(con -> {
                    PreparedStatement ps = con.prepareStatement(pair.left.toString(), new String[]{idField});
                    int i = 1;
                    for (Object obj : params.toArray()) {
                        ps.setObject(i++, obj);
                    }
                    return ps;
                }, keyHolder);
                if (rows <= 0) {
                    throw Exceptions.throwIllegalStateException("insert 操作失败");
                }
                Field field = JdbcModelManager.getPrimaryKeyField(clazz);
                if (!field.getType().isAssignableFrom(Long.class)) {
                    throw Exceptions.throwIllegalStateException("主键必须是Long类型");
                }
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(t, keyHolder.getKey().longValue());
                return rows;
            } else {
                //非sequence情况
                int rows = jdbcOperations.update(pair.left.toString(), params.toArray());
                if (rows <= 0) {
                    throw Exceptions.throwIllegalStateException("insert 操作失败");
                }
                return rows;
            }

        } catch (Exception e) {
            log.error("插入操作失败", e);
            throw Exceptions.throwIllegalStateException("插入操作失败", e);
        } finally {
            clear();
        }
    }


    @Override
    public <M> int[] insertBatch(List<M> list) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(list.get(0), false);

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
                    throw Exceptions.throwIllegalStateException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw Exceptions.throwIllegalStateException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JdbcModelManager.getAnnotationSequenceGenerator(clazz);
                    if (!sequenceGeneratorOptional.isPresent()) {
                        throw Exceptions.throwIllegalStateException("@SequenceGenerator注解不存在");
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
        log.info("sql语句为:" + stringBuilder.toString());

        //拼装参数
        List<Object[]> batchParams = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            List<Object> params = new ArrayList<>();
            List<ImmutablePair<String, Object>> tmp = JdbcModelManager.getTableColumnNameAndValue(list.get(j), false);
            for (int i = 0; i < tmp.size(); i++) {

                if(flag) {
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
}
