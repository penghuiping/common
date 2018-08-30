package com.php25.common.jdbc;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author: penghuiping
 * @date: 2018/8/29 23:12
 * @description:
 */
public class JpaRowMapper<T> implements RowMapper<T> {
    private Class<T> mapperClass;

    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    public JpaRowMapper(Class<T> mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Assert.notNull(mapperClass, "mapperClass不能为null");
        try {
            T model = mapperClass.newInstance();
            ModelMeta modelMeta = JpaModelManager.getModelMeta(mapperClass);
            List<String> dbColumns = modelMeta.getDbColumns();
            List<Class> columnTypes = modelMeta.getColumnTypes();
            for (int i = 0; i < dbColumns.size(); i++) {
                String dbColumn = dbColumns.get(i);
                String classColumn = JpaModelManager.getClassColumnByDbColumn(mapperClass, dbColumn);
                Object value = rs.getObject(dbColumn);
                Class<?> columnType = columnTypes.get(i);

                if (columnType.isPrimitive() || Number.class.isAssignableFrom(columnType) || String.class.isAssignableFrom(columnType) || Date.class.isAssignableFrom(columnType)) {
                    //基本类型 string,date 不需要处理
                    if (null != value) {
                        value = convertValueToRequiredType(value, columnType);
                    }
                    ReflectUtil.getMethod(mapperClass, "set" + StringUtil.capitalizeFirstLetter(classColumn), columnType).invoke(model, value);
                } else {
                    if (!(Collection.class.isAssignableFrom(columnType))) {
                        //自定义类，直接设置主键值
                        //设置子类主键
                        Object subObj = columnType.newInstance();
                        Field subClassPkField = JpaModelManager.getPrimaryKeyField(columnType);
                        if (null != value) {
                            value = convertValueToRequiredType(value, subClassPkField.getType());
                        }
                        ReflectUtil.getMethod(columnType, "set" + StringUtil.capitalizeFirstLetter(subClassPkField.getName()), subClassPkField.getType()).invoke(subObj, value);
                        //给自定义类赋值
                        ReflectUtil.getMethod(mapperClass, "set" + StringUtil.capitalizeFirstLetter(classColumn), columnType).invoke(model, subObj);
                    } else {
                        //List类型，不做任何处理
                    }
                }
            }
            return model;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("数据库数据转化为model出错,class:" + mapperClass.getName(), e);
        }
    }


    private Object convertValueToRequiredType(Object value, Class<?> requiredType) {
        if (String.class == requiredType) {
            return value.toString();
        } else if (Number.class.isAssignableFrom(requiredType)) {
            if (value instanceof Number) {
                // Convert original Number to target Number class.
                return NumberUtils.convertNumberToTargetClass(((Number) value), (Class<Number>) requiredType);
            } else {
                // Convert stringified value to target Number class.
                return NumberUtils.parseNumber(value.toString(), (Class<Number>) requiredType);
            }
        } else if (this.conversionService != null && this.conversionService.canConvert(value.getClass(), requiredType)) {
            return this.conversionService.convert(value, requiredType);
        } else {
            throw new IllegalArgumentException(
                    "Value [" + value + "] is of type [" + value.getClass().getName() +
                            "] and cannot be converted to required type [" + requiredType.getName() + "]");
        }
    }

}


