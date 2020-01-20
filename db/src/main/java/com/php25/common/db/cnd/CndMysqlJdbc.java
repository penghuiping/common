package com.php25.common.db.cnd;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.annotation.GeneratedValue;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2019/7/25 15:13
 * @description:
 */
public class CndMysqlJdbc extends CndJdbc {
    private static final Logger log = LoggerFactory.getLogger(CndMysqlJdbc.class);

    protected CndMysqlJdbc(Class cls, JdbcOperations jdbcOperations) {
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
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(JdbcModelManager.getTableName(clazz)).append("( ");
        List<ImmutablePair<String, Object>> pairList = JdbcModelManager.getTableColumnNameAndValue(t, ignoreNull);

        //判断是否是auto_increment
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
                    throw new DbException("抱歉!mysql不支持这种模式");
                case IDENTITY:
                    flag = true;
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
        log.info("sql语句为:" + stringBuilder.toString());
        try {
            if (flag) {
                //自增操作
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
                    throw new DbException("insert 操作失败");
                }
                Field field = JdbcModelManager.getPrimaryKeyField(clazz);
                if (!field.getType().isAssignableFrom(Long.class) && !field.getType().isAssignableFrom(long.class)) {
                    throw new DbException("自增主键必须是Long类型");
                }
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(t, keyHolder.getKey().longValue());
                return rows;
            } else {
                //非自增操作
                int rows = jdbcOperations.update(stringBuilder.toString(), params.toArray());
                if (rows <= 0) {
                    throw new DbException("insert 操作失败");
                }
                return rows;
            }
        } catch (Exception e) {
            log.error("插入操作失败", e);
            throw new DbException("插入操作失败", e);
        } finally {
            clear();
        }
    }
}
