package com.php25.common.jdbc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * 解析jpa注解与数据库model的对应关系帮助类
 *
 * @author: penghuiping
 * @date: 2018/8/9 18:11
 */
public class JpaModelManager {

    private static final Logger log = LoggerFactory.getLogger(JpaModelManager.class);

    private static ConcurrentReferenceHashMap<String, ModelMeta> modelMetas = new ConcurrentReferenceHashMap<>();

    /****
     * 根据实体class获取表名
     * @param cls
     * @return
     */
    public static String getTableName(Class<?> cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getDbTableName();
        } else {
            return JpaModelManagerHelper.getTableName(cls);
        }
    }

    /**
     * 获取实体对象的ModelMeta
     *
     * @param cls
     * @return
     */
    public static ModelMeta getModelMeta(Class<?> cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta;
        } else {
            modelMeta = JpaModelManagerHelper.getModelMeta(cls);
            modelMetas.putIfAbsent(cls.getName(), modelMeta);
            return modelMeta;
        }
    }

    /**
     * 获取model类的主键field
     *
     * @param cls model类对的class
     * @return model类反射对应的主键field
     */
    public static Field getPrimaryKeyField(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getPkField();
        } else {
            return JpaModelManagerHelper.getPrimaryKeyField(cls);
        }
    }

    /**
     * 获取model类,@version属性项
     *
     * @param cls model类对的class
     * @return model类反射对应的 version field
     */
    public static Optional<Field> getVersionField(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return Optional.ofNullable(modelMeta.getVersionField());
        } else {
            return JpaModelManagerHelper.getVersionField(cls);
        }
    }

    /**
     * 获取model表的主键字段名
     *
     * @param cls
     * @return
     */
    public static String getPrimaryKeyColName(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getDbPkName();
        } else {
            return JpaModelManagerHelper.getPrimaryKeyColName(cls);
        }
    }

    /**
     * 获取model的@GeneratedValue注解
     *
     * @param cls
     * @return
     */
    public static Optional<GeneratedValue> getAnnotationGeneratedValue(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return Optional.ofNullable(modelMeta.getGeneratedValue());
        } else {
            return JpaModelManagerHelper.getAnnotationGeneratedValue(cls);
        }
    }

    /**
     * 获取model的@SequenceGenerator注解
     *
     * @param cls
     * @return
     */
    public static Optional<SequenceGenerator> getAnnotationSequenceGenerator(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return Optional.ofNullable(modelMeta.getSequenceGenerator());
        } else {
            return JpaModelManagerHelper.getAnnotationSequenceGenerator(cls);
        }
    }

    /**
     * 获取model的主键类属性名
     *
     * @param cls
     * @return
     */
    public static String getPrimaryKeyFieldName(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getClassPkName();
        } else {
            return JpaModelManagerHelper.getPrimaryKeyFieldName(cls);
        }
    }

    /**
     * 根据类属性获取db属性
     *
     * @param cls
     * @param name
     * @return
     */
    public static String getDbColumnByClassColumn(Class cls, String name) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            List<String> classColumns = modelMeta.getClassColumns();
            List<String> dbColumns = modelMeta.getDbColumns();
            for (int i = 0; i < classColumns.size(); i++) {
                if (classColumns.get(i).equals(name)) {
                    return dbColumns.get(i);

                }
            }
            throw new RuntimeException("无法找到相对应的column");
        } else {
            return JpaModelManagerHelper.getDbColumnByClassColumn(cls, name);
        }
    }

    /**
     * 根据db属性获取类属性
     *
     * @param cls
     * @param name
     * @return
     */
    public static String getClassColumnByDbColumn(Class cls, String name) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            List<String> classColumns = modelMeta.getClassColumns();
            List<String> dbColumns = modelMeta.getDbColumns();
            String result = null;
            for (int i = 0; i < dbColumns.size(); i++) {
                if (dbColumns.get(i).equals(name)) {
                    result = classColumns.get(i);
                    break;
                }
            }
            return result;
        } else {
            return JpaModelManagerHelper.getClassColumnByDbColumn(cls, name);
        }
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
        return JpaModelManagerHelper.getTableColumnNameAndValue(t, ignoreNull);
    }
}
