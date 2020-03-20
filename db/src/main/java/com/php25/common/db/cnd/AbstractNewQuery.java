package com.php25.common.db.cnd;

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
