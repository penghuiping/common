package com.php25.common.jdbc;

import com.google.common.collect.Lists;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析jpa注解与数据库model的对应关系帮助类
 *
 * @author: penghuiping
 * @date: 2018/8/9 18:11
 */
public class JpaModelManagerHelper {

    private static final Logger log = LoggerFactory.getLogger(JpaModelManagerHelper.class);


    public static ModelMeta getModelMeta(Class<?> cls) {
        ModelMeta modelMeta = new ModelMeta();
        modelMeta.setDbTableName(getTableName(cls));
        Field[] fields = cls.getDeclaredFields();
        List<String> dbColumns = new ArrayList<>();
        List<String> classColumns = new ArrayList<>();
        List<Class> columnsType = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Transient tmp = field.getAnnotation(Transient.class);
            if (tmp == null && !Collection.class.isAssignableFrom(field.getType())) {
                classColumns.add(field.getName());
                Column column = field.getAnnotation(Column.class);
                String columnName = null;
                if (null == column) {
                    columnName = field.getName();
                } else {
                    columnName = StringUtil.isBlank(column.name()) ? field.getName() : column.name();
                }
                dbColumns.add(columnName);
                columnsType.add(field.getType());
            }
        }
        modelMeta.setClassColumns(classColumns);
        modelMeta.setDbColumns(dbColumns);
        modelMeta.setColumnTypes(columnsType);
        modelMeta.setPkField(getPrimaryKeyField(cls));
        modelMeta.setDbPkName(getPrimaryKeyColName(cls));
        modelMeta.setClassPkName(getPrimaryKeyFieldName(cls));
        Optional<Field> versionOptional = getVersionField(cls);
        versionOptional.ifPresent(modelMeta::setVersionField);
        Optional<GeneratedValue> generatedValueOptional = getAnnotationGeneratedValue(cls);
        generatedValueOptional.ifPresent(modelMeta::setGeneratedValue);
        Optional<SequenceGenerator> sequenceGeneratorOptional = getAnnotationSequenceGenerator(cls);
        sequenceGeneratorOptional.ifPresent(modelMeta::setSequenceGenerator);
        return modelMeta;
    }

    /****
     * 根据实体class获取表名
     * @param cls
     * @return
     */
    protected static String getTableName(Class<?> cls) {
        Assert.notNull(cls, "class不能为null");
        Entity entity = cls.getAnnotation(Entity.class);
        if (null == entity) {
            throw new IllegalArgumentException(cls.getName() + ":没有javax.persistence.Entity注解");
        }

        Table table = cls.getAnnotation(Table.class);
        if (null == table) {
            throw new IllegalArgumentException(cls.getName() + ":没有javax.persistence.Table注解");
        }

        //获取表名
        String tableName = table.name();
        if (StringUtil.isBlank(tableName)) {
            return cls.getSimpleName();
        } else {
            return tableName;
        }
    }

    /**
     * 获取model类的主键field
     *
     * @param cls model类对的class
     * @return model类反射对应的主键field
     */
    protected static Field getPrimaryKeyField(Class cls) {
        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKeyField = field;
                break;
            }
        }
        if (null == primaryKeyField) {
            throw new RuntimeException("此类没有用@Id主键");
        }
        return primaryKeyField;
    }

    /**
     * 获取model类,@version属性项
     *
     * @param cls model类对的class
     * @return model类反射对应的 version field
     */
    protected static Optional<Field> getVersionField(Class cls) {
        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        Field versionField = null;
        for (Field field : fields) {
            Version version = field.getAnnotation(Version.class);
            if (version != null) {
                versionField = field;
                break;
            }
        }
        return Optional.ofNullable(versionField);
    }

    /**
     * 获取model表的主键字段名
     *
     * @param cls
     * @return
     */
    protected static String getPrimaryKeyColName(Class cls) {
        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKeyField = field;
                break;
            }
        }
        if (null == primaryKeyField) {
            throw new RuntimeException("此类没有用@Id主键");
        }

        String pkName = primaryKeyField.getName();
        Column column = primaryKeyField.getAnnotation(Column.class);
        if (null != column && !StringUtil.isBlank(column.name())) {
            pkName = column.name();
        }

        return pkName;
    }

    /**
     * 获取model的@GeneratedValue注解
     *
     * @param cls
     * @return
     */
    protected static Optional<GeneratedValue> getAnnotationGeneratedValue(Class cls) {
        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        GeneratedValue generatedValue = null;
        for (Field field : fields) {
            generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                break;
            }
        }
        return Optional.ofNullable(generatedValue);
    }

    /**
     * 获取model的@SequenceGenerator注解
     *
     * @param cls
     * @return
     */
    protected static Optional<SequenceGenerator> getAnnotationSequenceGenerator(Class cls) {

        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        SequenceGenerator generatedValue = null;
        for (Field field : fields) {
            generatedValue = field.getAnnotation(SequenceGenerator.class);
            if (generatedValue != null) {
                break;
            }
        }
        return Optional.ofNullable(generatedValue);
    }

    /**
     * 获取model的主键类属性名
     *
     * @param cls
     * @return
     */
    protected static String getPrimaryKeyFieldName(Class cls) {
        Assert.notNull(cls, "class不能为null");
        Field[] fields = cls.getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKeyField = field;
                break;
            }
        }
        if (null == primaryKeyField) {
            throw new RuntimeException("此类没有用@Id主键");
        }
        return primaryKeyField.getName();
    }

    /**
     * 根据类属性获取db属性
     *
     * @param cls
     * @param name
     * @return
     */
    protected static String getDbColumnByClassColumn(Class cls, String name) {
        Assert.notNull(cls, "class不能为null");
        Assert.hasText(name, "name不能为空");
        Field[] fields = cls.getDeclaredFields();
        Optional<Field> fieldOptional = Lists.newArrayList(fields).stream().filter(field -> field.getName().equals(name)).findFirst();
        if (!fieldOptional.isPresent()) {
            throw new RuntimeException(String.format("%s类的%s属性不存在", cls.getSimpleName(), name));
        }
        Column column = fieldOptional.get().getAnnotation(Column.class);
        String columnName = null;
        if (null == column) {
            columnName = name;
        } else {
            columnName = StringUtil.isBlank(column.name()) ? name : column.name();
        }
        return columnName;
    }

    /**
     * 根据db属性获取类属性
     *
     * @param cls
     * @param name
     * @return
     */
    protected static String getClassColumnByDbColumn(Class cls, String name) {
        Assert.notNull(cls, "class不能为null");
        Assert.hasText(name, "name不能为空");
        Field[] fields = cls.getDeclaredFields();
        Optional<Field> fieldOptional = Lists.newArrayList(fields).stream().filter(field -> {
            Column column = field.getAnnotation(Column.class);
            String columnName = StringUtil.isBlank(column.name()) ? field.getName() : column.name();
            if (columnName.equals(name)) {
                return true;
            } else {
                return false;
            }
        }).findFirst();
        if (!fieldOptional.isPresent()) {
            throw new RuntimeException(String.format("%s类的%s表属性不存在", cls.getSimpleName(), name));
        }
        return fieldOptional.get().getName();
    }

    /**
     * 获取表属性列名与值
     *
     * @param t
     * @param ignoreNull
     * @param <T>
     * @return
     */
    protected static <T> List<ImmutablePair<String, Object>> getTableColumnNameAndValue(T t, boolean ignoreNull) {
        Assert.notNull(t, "t不能为null");
        Field[] fields = t.getClass().getDeclaredFields();
        Stream<ImmutablePair<String, Object>> stream = Lists.newArrayList(fields).stream()
                .filter(field -> (null == field.getAnnotation(Transient.class)) && (!Collection.class.isAssignableFrom(field.getType())))
                .map(field1 -> {
                    Column column = field1.getAnnotation(Column.class);
                    String fieldName = field1.getName();
                    String columnName = null;
                    if (null == column) {
                        columnName = fieldName;
                    } else {
                        columnName = StringUtil.isBlank(column.name()) ? fieldName : column.name();
                    }
                    Object value = null;
                    try {
                        value = ReflectUtil.getMethod(t.getClass(), "get" + StringUtil.capitalizeFirstLetter(fieldName)).invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    return new ImmutablePair<>(columnName, value);
                });
        List<ImmutablePair<String, Object>> pairList = null;
        if (ignoreNull) {
            String id = JpaModelManagerHelper.getPrimaryKeyColName(t.getClass());
            pairList = stream.filter(pair -> (pair.getLeft().equals(id) || pair.right != null)).collect(Collectors.toList());
        } else {
            pairList = stream.collect(Collectors.toList());
        }
        return pairList;
    }
}
