package com.php25.common.db.core.sql;

/**
 * @author penghuiping
 * @date 2021/12/26 15:11
 */
public interface Column {

    /**
     * 获取字段名
     *
     * @return 字段名
     */
    String getName();

    /**
     * 获取实体别名
     *
     * @return 实体别名
     */
    String getEntityAlias();
}
