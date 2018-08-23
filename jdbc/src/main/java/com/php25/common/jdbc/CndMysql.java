package com.php25.common.jdbc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

import javax.persistence.GeneratedValue;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2018/8/16 10:14
 */
public class CndMysql extends Cnd {
    private static final Logger log = LoggerFactory.getLogger(CndMysql.class);

    protected CndMysql(Class cls, JdbcOperations jdbcOperations) {
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
            sb.append(String.format("limit %s,%s", startRow, pageSize)).append(" ");
        }
    }

    @Override
    protected <T> int insert(T t, boolean ignoreNull) {
        //泛型获取类所有的属性
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + JpaModelManager.getTableName(clazz) + "( ");
        List<ImmutablePair<String, Object>> pairList = JpaModelManager.getTableColumnNameAndValue(t, ignoreNull);

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
                    throw new RuntimeException("抱歉!mysql不支持这种模式");
                case IDENTITY:
                    //由于使用了mysql auto-increment 所以直接移除id
                    String id = JpaModelManager.getPrimaryKeyColName(clazz);
                    pairList = pairList.stream().filter(stringObjectImmutablePair -> !stringObjectImmutablePair.getLeft().equals(id)).collect(Collectors.toList());
                    break;
                case SEQUENCE:
                    throw new RuntimeException("抱歉!mysql不支持这种模式");
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
            params.add(pairList.get(i).getRight());
        }
        stringBuilder.append(" )");
        log.info("sql语句为:" + stringBuilder.toString());
        try {
            return jdbcOperations.update(stringBuilder.toString(), params.toArray());
        } catch (Exception e) {
            log.error("插入操作失败", e);
            throw new RuntimeException("插入操作失败", e);
        } finally {
            clear();
        }
    }
}
