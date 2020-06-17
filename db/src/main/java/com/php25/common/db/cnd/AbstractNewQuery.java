package com.php25.common.db.cnd;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: penghuiping
 * @date: 2018/9/4 23:24
 * @description:
 */
public abstract class AbstractNewQuery extends AbstractQuery {
    protected Class<?> clazz;

    protected String clazzAlias;

    protected Map<String, Class<?>> aliasMap = new HashMap<>(8);

    @Override
    public String getCol(String name) {
        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            if (parts.length == 2) {
                //先尝试从aliasMap中获取
                Class<?> modelClass = aliasMap.getOrDefault(parts[0], null);
                if (null == modelClass) {
                    //不存在 没使用别名，试试是否是类名
                    modelClass = JdbcModelManager.getClassFromModelName(parts[0]);
                    return getCol(modelClass, null, parts[1]);
                } else {
                    //存在说明使用了别名
                    return getCol(modelClass, parts[0], parts[1]);
                }
            } else {
                throw new DbException("Db Column name is illegal");
            }
        }
        return getCol(clazz, null, name);
    }

    protected String getCol(Class<?> modelClass, String alias, String name) {
        try {
            if (StringUtil.isBlank(alias)) {
                //没有使用别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" %s.%s ", JdbcModelManager.getTableName(modelClass), JdbcModelManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" %s.%s ", JdbcModelManager.getTableName(this.clazz), JdbcModelManager.getDbColumnByClassColumn(this.clazz, name));
                }
            } else {
                //使用了别名
                if (!clazz.equals(modelClass)) {
                    return String.format(" %s.%s ", alias, JdbcModelManager.getDbColumnByClassColumn(modelClass, name));
                } else {
                    return String.format(" %s.%s ", alias, JdbcModelManager.getDbColumnByClassColumn(this.clazz, name));
                }
            }
        } catch (Exception e) {
            //"无法通过jpa注解找到对应的column,直接调用父类的方法"
            return super.getCol(name);
        }
    }

    @Override
    public Query whereOneEqualOne() {
        this.getSql().append(" ").append(WHERE).append(" 1=1 ");
        return this;
    }
}
