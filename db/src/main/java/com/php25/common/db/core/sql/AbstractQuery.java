package com.php25.common.db.core.sql;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.mapper.JdbcModelCacheManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 抽象查询实现，可以作为查询条件实现类的基类，实现一些基础的方法，方便查询条件实现类，实现功能
 *
 * @author penghuiping
 * @date 2021/12/26 15:22
 */
public abstract class AbstractQuery {
    protected QueryContext queryContext;

    public AbstractQuery(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    /**
     * insert时候进行参数转化
     * <p>
     * 对于自定义类型class，需要获取这个class的primary key值
     *
     * @param paramValue 源参数值
     * @return 最终参数值
     */
    static Object paramConvert(Object paramValue) {
        if (null == paramValue) {
            return null;
        }
        Class<?> paramValueType = paramValue.getClass();
        if (paramValueType.isPrimitive() || Boolean.class.isAssignableFrom(paramValueType) || Number.class.isAssignableFrom(paramValueType) || String.class.isAssignableFrom(paramValueType)) {
            //基本类型,string,date直接加入参数列表
            return paramValue;
        } else if (Date.class.isAssignableFrom(paramValueType)) {
            Date tmp = (Date) paramValue;
            return new Timestamp(tmp.getTime());
        } else if (LocalDateTime.class.isAssignableFrom(paramValueType)) {
            LocalDateTime tmp = (LocalDateTime) paramValue;
            return new Timestamp(Date.from(tmp.toInstant(ZoneOffset.ofHours(8))).getTime());
        } else {
            if (!(Collection.class.isAssignableFrom(paramValueType))) {
                //自定义class类，通过反射获取主键值，在加入参数列表
                String subClassPk = JdbcModelCacheManager.getPrimaryKeyFieldName(paramValueType);
                try {
                    return ReflectUtil.getMethod(paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)).invoke(paramValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DbException(String.format("%s没有%s方法", paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)), e);
                }
            } else {
                //Collection类型不做任何处理
                throw new DbException("此orm框架中model中不支持Collection类型的属性");
            }
        }
    }

    /**
     * 获取字段名
     *
     * @param name 字段名
     * @return 字段名
     */
    protected String getCol(String name) {
        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            if (parts.length == 2) {
                //先尝试从aliasMap中获取
                Class<?> modelClass = this.queryContext.getAliasMap().getOrDefault(parts[0], null);
                if (null == modelClass) {
                    //不存在 没使用别名，试试是否是类名
                    modelClass = JdbcModelCacheManager.getClassFromModelName(parts[0]);
                    if (modelClass == null) {
                        //不是，则使用原来的字符串
                        return " " + name + " ";
                    }
                    return getCol(modelClass, null, parts[1]);
                } else {
                    //存在说明使用了别名
                    return getCol(modelClass, parts[0], parts[1]);
                }
            } else {
                throw new DbException("Db Column name is illegal");
            }
        }
        return getCol(this.queryContext.getClazz(), null, name);
    }

    /**
     * 获取字段名
     *
     * @param modelClass 实体类
     * @param alias      实体别名
     * @param name       字段名
     * @return 字段名
     */
    protected String getCol(Class<?> modelClass, String alias, String name) {
        try {
            Class<?> clazz = this.queryContext.getClazz();
            if (StringUtil.isBlank(alias)) {
                //没有使用别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" ${%s}.%s ", modelClass.getSimpleName(), JdbcModelCacheManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" ${%s}.%s ", clazz.getSimpleName(), JdbcModelCacheManager.getDbColumnByClassColumn(clazz, name));
                }
            } else {
                //使用了别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" %s.%s ", alias, JdbcModelCacheManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" %s.%s ", alias, JdbcModelCacheManager.getDbColumnByClassColumn(clazz, name));
                }
            }
        } catch (Exception e) {
            //"无法通过注解找到对应的column,直接使用传入的名字符串"
            return " " + name + " ";
        }
    }

    /**
     * 拼接SQL
     *
     * @param sqlPart sql
     */
    protected AbstractQuery appendSql(String sqlPart) {
        StringBuilder sql = queryContext.getSql();
        if (sql == null) {
            sql = new StringBuilder();
        }
        sql.append(sqlPart);
        this.queryContext.setSql(sql);
        return this;
    }

    /**
     * 增加参数
     *
     * @param object 查询参数
     * @return 查询
     */
    protected AbstractQuery addParam(Object object) {
        this.queryContext.getParams().add(object);
        return this;
    }

    /**
     * 增加参数
     *
     * @param objects 查询参数
     * @return 查询
     */
    protected AbstractQuery addParam(Collection<?> objects) {
        this.queryContext.getParams().addAll(objects);
        return this;
    }

    /**
     * 在头部增加参数
     *
     * @param objects 查询参数
     * @return 查询
     */
    protected AbstractQuery addPreParam(List<Object> objects) {
        objects.addAll(this.queryContext.getParams());
        this.queryContext.setParams(objects);
        return this;
    }

    protected void appendAndSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, DbConstant.AND);
    }

    protected void appendOrSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, DbConstant.OR);
    }

    protected void appendSqlBase(String column, Object value, String opt, String link) {
        if (this.queryContext.getSql().indexOf(DbConstant.WHERE) < 0) {
            link = DbConstant.WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt);
        if (value != null) {
            this.appendSql(" ? ");
            this.addParam(value);
        }
    }

    protected void appendInSql(String column, Collection<?> value, String opt, String link) {
        StringBuilder sql = this.queryContext.getSql();
        if (sql.indexOf(DbConstant.WHERE) < 0) {
            link = DbConstant.WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ( ");
        for (Object o : value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        sql = this.queryContext.getSql();
        sql.deleteCharAt(sql.length() - 1);
        this.appendSql(" ) ");
    }

    protected void appendBetweenSql(String column, String opt, String link, Object... value) {
        StringBuilder sql = this.queryContext.getSql();
        if (sql.indexOf(DbConstant.WHERE) < 0) {
            link = DbConstant.WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ? AND ? ");
        this.addParam(value[0]);
        this.addParam(value[1]);
    }

    protected Query manyCondition(Query condition, String link) {
        if (condition == null) {
            throw new DbException("连接条件必须是一个 QueryCondition 类型");
        }

        //去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(DbConstant.WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }

        appendSql(link)
                .appendSql(" (")
                .appendSql(condition.getSql().toString())
                .appendSql(")");
        addParam(condition.getParams());
        return this.queryContext.getQuery();
    }

    public List<Object> getParams() {
        return this.queryContext.getParams();
    }

    public StringBuilder getSql() {
        return this.queryContext.getSql();
    }
}
