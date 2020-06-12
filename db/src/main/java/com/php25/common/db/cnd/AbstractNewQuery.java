package com.php25.common.db.cnd;

import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;

/**
 * @author: penghuiping
 * @date: 2018/9/4 23:24
 * @description:
 */
public abstract class AbstractNewQuery extends AbstractQuery {
    Class clazz;

    @Override
    public String getCol(String name) {
        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            if (parts.length == 2) {
                Class<?> modelClass = JdbcModelManager.getClassFromModelName(parts[0]);
                return getCol(modelClass, parts[1]);
            } else {
                throw new DbException("Db Column name is illegal");
            }
        }
        return getCol(clazz, name);
    }

    protected String getCol(Class<?> modelClass, String name) {
        try {

            if (!clazz.equals(modelClass)) {
                return String.format(" %s.%s ", JdbcModelManager.getTableName(modelClass), JdbcModelManager.getDbColumnByClassColumn(modelClass, name));
            } else {
                return String.format(" %s.%s ", JdbcModelManager.getTableName(this.clazz), JdbcModelManager.getDbColumnByClassColumn(this.clazz, name));
            }
        } catch (Exception e) {
            //"无法通过jpa注解找到对应的column,直接调用父类的方法"
            return super.getCol(name);
        }
    }
}
