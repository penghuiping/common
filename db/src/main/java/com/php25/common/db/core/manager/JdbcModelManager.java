package com.php25.common.db.core.manager;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.Db;
import com.php25.common.db.core.annotation.GeneratedValue;
import com.php25.common.db.core.annotation.SequenceGenerator;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.shard.ShardingKey;
import com.php25.common.db.exception.DbException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: penghuiping
 * @date: 2019/7/25 14:32
 * @description:
 */
public class JdbcModelManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcModelManager.class);

    private static final ConcurrentReferenceHashMap<String, ModelMeta> modelMetas = new ConcurrentReferenceHashMap<>(512);

    private static final ConcurrentReferenceHashMap<String, Class<?>> modelNameToClass = new ConcurrentReferenceHashMap<>(512);

    /****
     * 根据实体class获取逻辑表名
     * @param cls 实体类
     * @return 逻辑表名
     */
    public static String getLogicalTableName(Class<?> cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getLogicalTableName();
        } else {
            if (JdbcModelManagerHelper.isShardTable(cls)) {
                return JdbcModelManagerHelper.getShardTableName(cls).getLeft();
            } else {
                return JdbcModelManagerHelper.getTableName(cls);
            }
        }
    }

    /**
     * 根据实体class获取物理表名
     *
     * @param cls 实体类
     * @return 物理表名
     */
    public static String[] getPhysicalTableName(Class<?> cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return modelMeta.getPhysicalTableNames();
        } else {
            if (JdbcModelManagerHelper.isShardTable(cls)) {
                return JdbcModelManagerHelper.getShardTableName(cls).getRight();
            } else {
                return new String[]{JdbcModelManagerHelper.getTableName(cls)};
            }
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
            modelMeta = JdbcModelManagerHelper.getModelMeta(cls);
            modelMetas.putIfAbsent(cls.getName(), modelMeta);
            modelNameToClass.putIfAbsent(cls.getSimpleName(), cls);
            return modelMeta;
        }
    }

    /**
     * 通过modelName获取对应的实体类
     *
     * @param modelName
     * @return
     */
    public static Class<?> getClassFromModelName(String modelName) {
        return modelNameToClass.get(modelName);
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
            log.warn("getPrimaryKeyField没有使用缓存");
            return JdbcModelManagerHelper.getPrimaryKeyField(cls);
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
            log.warn("getVersionField没有使用缓存");
            return JdbcModelManagerHelper.getVersionField(cls);
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
            log.warn("getPrimaryKeyColName没有使用缓存");
            return JdbcModelManagerHelper.getPrimaryKeyColName(cls);
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
            log.warn("getAnnotationGeneratedValue没有使用缓存");
            return JdbcModelManagerHelper.getAnnotationGeneratedValue(cls);
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
            log.warn("getAnnotationSequenceGenerator没有使用缓存");
            return JdbcModelManagerHelper.getAnnotationSequenceGenerator(cls);
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
            log.warn("getPrimaryKeyFieldName没有使用缓存");
            return JdbcModelManagerHelper.getPrimaryKeyFieldName(cls);
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
            throw new DbException("无法找到相对应的column");
        } else {
            log.warn("getDbColumnByClassColumn没有使用缓存");
            return JdbcModelManagerHelper.getDbColumnByClassColumn(cls, name);
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
            log.warn("getClassColumnByDbColumn没有使用缓存");
            return JdbcModelManagerHelper.getClassColumnByDbColumn(cls, name);
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
        return JdbcModelManagerHelper.getTableColumnNameAndValue(t, ignoreNull);
    }

    /**
     * 获取表属性列名与值
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<ImmutablePair<String, Object>> getTableColumnNameAndCollectionValue(T t) {
        return JdbcModelManagerHelper.getTableColumnNameAndCollectionValue(t);
    }

    /**
     * 获取主键的值
     *
     * @param clazz
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Object getPrimaryKeyValue(Class<?> clazz, T t) {
        //获取主键值
        String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);
        Object id = null;
        try {
            id = ReflectUtil.getMethod(clazz, "get" + StringUtil.capitalizeFirstLetter(idField)).invoke(t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DbException(String.format("%s类%s方法反射调用出错", clazz.getName(), "get" + StringUtil.capitalizeFirstLetter(idField)));
        }
        return id;
    }

    /**
     * 判断model是否存在集合属性
     *
     * @param cls
     * @return
     */
    public static Boolean existCollectionAttribute(Class cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta && null != modelMeta.getExistCollectionAttribute()) {
            return modelMeta.getExistCollectionAttribute();
        } else {
            log.warn("existCollectionAttribute 没有使用缓存");
            return JdbcModelManagerHelper.existCollectionAttribute(cls);
        }
    }

    /**
     * 判断model是否是分片表
     *
     * @param cls model类
     * @return true:是分片表 false:不是分片表
     */
    public static Boolean isShardTable(Class<?> cls) {
        ModelMeta modelMeta = modelMetas.get(cls.getName());
        if (null != modelMeta) {
            return null != modelMeta.getPhysicalTableNames() && modelMeta.getPhysicalTableNames().length > 1;
        } else {
            log.warn("existCollectionAttribute 没有使用缓存");
            return JdbcModelManagerHelper.isShardTable(cls);
        }
    }

    public static Optional<ShardingKey> getAnnotationShardingKey(Class<?> modelClass) {
        ModelMeta modelMeta = modelMetas.get(modelClass.getName());
        if (null != modelMeta) {
            return Optional.ofNullable(modelMeta.getShardingKey());
        } else {
            log.warn("getAnnotationShardingKey 没有使用缓存");
            return JdbcModelManagerHelper.getAnnotationShardingKey(modelClass);
        }
    }

    public static ShardRule getShardRule(Class<?> modelClass) {
        Optional<ShardingKey> shardingKeyOptional = JdbcModelManager.getAnnotationShardingKey(modelClass);
        if (!shardingKeyOptional.isPresent()) {
            throw new DbException("分片表实体必须需要@ShardingKey注解");
        }
        ShardRule shardRule = SpringContextHolder.getBean0(shardingKeyOptional.get().shardRule());
        return shardRule;
    }

    /**
     * 获取shardingKey的值
     *
     * @param modelClass 实体对象类
     * @param model      实体对象
     * @return shardingKey的值
     */
    public static Object getShardingKeyValue(Class<?> modelClass, Object model) {
        AssertUtil.notNull(modelClass, "modelClass不能为null");
        AssertUtil.notNull(model, "model不能为null");
        Field[] fields = modelClass.getDeclaredFields();
        Field shardingKeyField = null;
        ShardingKey shardingKey = null;
        Object shardingKeyValue = null;
        for (Field field : fields) {
            shardingKey = field.getAnnotation(ShardingKey.class);
            if (shardingKey != null) {
                shardingKeyField = field;
                break;
            }
        }
        if (null == shardingKeyField) {
            return null;
        }

        //获取shardingKey的值
        try {
            shardingKeyValue = ReflectUtil.getMethod(modelClass, "get" + StringUtil.capitalizeFirstLetter(shardingKeyField.getName())).invoke(model);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DbException(e.getMessage(), e);
        }
        return shardingKeyValue;
    }

    public static List<JdbcTemplate> getAllShardingDbs(Class<?> modelClass) {
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(modelClass);
        String[] physicNames = modelMeta.getPhysicalTableNames();
        List<JdbcTemplate> dbs = new ArrayList<>();
        for (int i = 0; i < physicNames.length; i++) {
            String physicName = physicNames[i];
            String[] physicNameSplit = physicName.split("\\.");
            String dbBeanName = physicNameSplit[0];
            Db db = (Db) SpringContextHolder.getApplicationContext().getBean(dbBeanName);
            dbs.add(db.getJdbcPair().getJdbcTemplate());
        }
        return dbs;
    }
}
