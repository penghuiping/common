package com.php25.common.db.cnd.sql;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/2 15:44
 */
public class SqlParams {

    private String sql;

    private List<Object> params;

    private List<Object[]> batchParams;

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
}
