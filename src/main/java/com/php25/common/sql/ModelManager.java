package com.php25.common.sql;

import com.google.common.collect.Lists;
import com.php25.common.util.ReflectUtil;
import com.php25.common.util.StringUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 18:11
 * @Description:
 */
public class ModelManager {

    private static final Logger log = LoggerFactory.getLogger(ModelManager.class);

    /****
     * 根据实体class获取表名
     * @param c
     * @return
     */
    public static String getTableName(Class<?> c) {
        Entity entity = c.getAnnotation(Entity.class);
        if (null == entity) throw new IllegalArgumentException(c.getName() + ":没有javax.persistence.Entity注解");

        Table table = c.getAnnotation(Table.class);
        if (null == table) throw new IllegalArgumentException(c.getName() + ":没有javax.persistence.Table注解");

        //获取表名
        String tableName = table.name();
        if (StringUtil.isBlank(tableName)) {
            return c.getSimpleName();
        } else {
            return tableName;
        }
    }

    /**
     * 获取model的主键名
     *
     * @param t
     * @return
     */
    public static <T> Field getPrimaryKeyColName(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKeyField = field;
                break;
            }
        }
        if (null == primaryKeyField) throw new RuntimeException("此类没有用@Id主键");
        return primaryKeyField;
    }

    /**
     * 获取表属性列名与值
     *
     * @param t
     * @param ignoreNull
     * @param <T>
     * @return
     */
    public static <T> List<ImmutablePair<String, Object>> getTableColumnNameAndValue(T t, boolean ignoreNull) {
        Field[] fields = t.getClass().getDeclaredFields();
        Stream<ImmutablePair<String, Object>> stream = Lists.newArrayList(fields).stream().filter(field -> null == field.getAnnotation(Transient.class)).map(field1 -> {
            Column column = field1.getAnnotation(Column.class);
            String fieldName = field1.getName();
            String columnName = null;
            if (null == column)
                columnName = fieldName;
            else
                columnName = StringUtil.isBlank(column.name()) ? fieldName : column.name();
            Object value = null;
            try {
                value = ReflectUtil.getMethod(t.getClass(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length())).invoke(t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return new ImmutablePair<>(columnName, value);
        });
        List<ImmutablePair<String, Object>> pairList = null;
        if (ignoreNull) {
            pairList = stream.filter(pair -> pair.right != null).collect(Collectors.toList());
        } else {
            pairList = stream.collect(Collectors.toList());
        }
        return pairList;
    }
}
