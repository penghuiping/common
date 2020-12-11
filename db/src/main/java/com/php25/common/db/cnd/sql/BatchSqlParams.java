package com.php25.common.db.cnd.sql;

import com.php25.common.db.cnd.GenerationType;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/11 15:25
 */
public class BatchSqlParams extends SqlParams {

    /**
     * 需要执行的sql
     */
    private String sql;

    /**
     * 参数,用于批量sql操作
     */
    private List<Object[]> batchParams;

    /**
     * 实体对象的类
     */
    private Class<?> clazz;

    /**
     * 实体对象
     */
    private Object model;

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

    public List<Object[]> getBatchParams() {
        return batchParams;
    }

    public void setBatchParams(List<Object[]> batchParams) {
        this.batchParams = batchParams;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
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
