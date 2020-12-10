package com.php25.common.db.cnd.sql;

import com.google.common.collect.Lists;
import com.php25.common.db.cnd.GenerationType;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/2 15:44
 */
public class SqlParams {

    private String sql;

    private List<Object> params = Lists.newArrayList();

    private List<Object[]> batchParams;

    private Class<?> clazz;

    private Object model;

    private GenerationType generationType;

    private Class<?> resultType;

    private String[] columns;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
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

    public GenerationType getGenerationType() {
        return generationType;
    }

    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
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
