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

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2019/10/21 10:55
 */
public class CndPostgresJdbc extends CndJdbc {

    private static final Logger log = LoggerFactory.getLogger(CndPostgresJdbc.class);

    protected CndPostgresJdbc(Class cls, JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.clazz = cls;
        this.dbType = DbType.POSTGRES;
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
            sb.append(String.format("limit %s offset %s", pageSize, startRow)).append(" ");
        }
    }

    @Override
    protected <T> int insert(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
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
                    throw Exceptions.throwIllegalStateException("抱歉!postgres不支持这种模式");
                case IDENTITY:
                    throw Exceptions.throwIllegalStateException("抱歉!postgres不支持这种模式");
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
                                return new ImmutablePair<String, Object>(pair.getLeft(), String.format("nextval('%s')", sequenceName));
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
        try {
            if (flag) {
                //sequence情况
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);

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
                    throw Exceptions.throwIllegalStateException("insert 操作失败");
                }
                Field field = JdbcModelManager.getPrimaryKeyField(clazz);
                if (!field.getType().isAssignableFrom(Long.class)) {
                    throw Exceptions.throwIllegalStateException("主键必须是Long类型");
                }
                Long id1 = (Long)keyHolder.getKeys().get(field.getName());
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(t, id1);
                return rows;
            } else {
                //非sequence情况
                int rows = jdbcOperations.update(stringBuilder.toString(), params.toArray());
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


}
