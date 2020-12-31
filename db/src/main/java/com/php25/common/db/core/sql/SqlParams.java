package com.php25.common.db.core.sql;

import com.php25.common.db.core.GenerationType;


/**
 * @author penghuiping
 * @date 2020/12/2 15:44
 */
public abstract class SqlParams {

    /**
     * 需要执行的sql
     */
    private String sql;

    /**
     * 实体对象的类
     */
    private Class<?> clazz;


    /**
     * id生成方式
     */
    private GenerationType generationType;

    /**
     * 映射成的类型
     */
    private Class<?> resultType;

    /**
     * 需要映射的字段
     */
    private String[] columns;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public GenerationType getGenerationType() {
        return generationType;
    }

    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }
}
