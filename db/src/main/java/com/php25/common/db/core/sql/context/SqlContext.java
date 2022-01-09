package com.php25.common.db.core.sql.context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/9 21:31
 */
public class SqlContext {
    private final List<Object> params = new ArrayList<>();
    private String sql;

    public List<Object> getParams() {
        return this.params;
    }

    public void addParam(Object value) {
        this.params.add(value);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
