package com.php25.common.jdbc;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2018/8/16 10:32
 */
public class CndOracle extends Cnd {
    private static final Logger log = LoggerFactory.getLogger(CndOracle.class);

    protected CndOracle(Class cls, JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.clazz = cls;
        this.dbType = DbType.MYSQL;
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

    @Override
    protected <T> int insert(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + JpaModelManager.getTableName(clazz) + "( ");
        List<ImmutablePair<String, Object>> pairList = JpaModelManager.getTableColumnNameAndValue(t, ignoreNull);

        //获取主键名
        String id = JpaModelManager.getPrimaryKeyColName(clazz);

        //是否是使用了sequence的情况
        boolean flag = false;

        //判断主键属性上是否有@GeneratedValue注解
        Optional<GeneratedValue> generatedValueOptional = JpaModelManager.getAnnotationGeneratedValue(clazz);
        if (generatedValueOptional.isPresent()) {
            //判断策略
            GeneratedValue generatedValue = generatedValueOptional.get();
            switch (generatedValue.strategy()) {
                case AUTO:
                    //程序指定,什么也不需要做
                    break;
                case TABLE:
                    throw new RuntimeException("抱歉!oracle不支持这种模式");
                case IDENTITY:
                    throw new RuntimeException("抱歉!oracle不支持这种模式");
                case SEQUENCE:
                    flag = true;
                    Optional<SequenceGenerator> sequenceGeneratorOptional = JpaModelManager.getAnnotationSequenceGenerator(clazz);
                    if (!sequenceGeneratorOptional.isPresent()) {
                        throw new RuntimeException("@SequenceGenerator注解不存在");
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
                        params.add(pairList.get(i).getRight());
                    }
                } else {
                    if (pairList.get(i).getLeft().equals(id)) {
                        stringBuilder.append(pairList.get(i).getRight()).append(",");
                    } else {
                        stringBuilder.append("?,");
                        params.add(pairList.get(i).getRight());
                    }
                }

            }
        } else {
            //非sequence情况
            for (int i = 0; i < pairList.size(); i++) {
                if (i == (pairList.size() - 1)) {
                    stringBuilder.append("?");
                    params.add(pairList.get(i).getRight());
                } else {
                    stringBuilder.append("?,");
                    params.add(pairList.get(i).getRight());
                }

            }
        }

        stringBuilder.append(" )");
        log.info("sql语句为:" + stringBuilder.toString());
        try {
            if (flag) {
                //sequence情况
                //获取id field名
                String idField = JpaModelManager.getPrimaryKeyFieldName(clazz);

                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = jdbcOperations.update(con -> {
                    PreparedStatement ps = con.prepareStatement(stringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
                    int i = 1;
                    for (Object obj : params.toArray()) {
                        ps.setObject(i++, obj);
                    }
                    return ps;
                }, keyHolder);
                if (rows <= 0) {
                    throw new RuntimeException("insert 操作失败");
                }
                Field field = JpaModelManager.getPrimaryKeyField(clazz);
                if (!field.getType().isAssignableFrom(Long.class)) {
                    throw new RuntimeException("主键必须是Long类型");
                }
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(t, keyHolder.getKey().longValue());
                return rows;
            } else {
                //非sequence情况
                int rows = jdbcOperations.update(stringBuilder.toString(), params.toArray());
                if (rows <= 0) {
                    throw new RuntimeException("insert 操作失败");
                }
                return rows;
            }

        } catch (Exception e) {
            log.error("插入操作失败", e);
            throw new RuntimeException("插入操作失败", e);
        } finally {
            clear();
        }
    }
}
