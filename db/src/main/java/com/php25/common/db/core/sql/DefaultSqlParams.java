package com.php25.common.db.core.sql;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/11 15:24
 */
public class DefaultSqlParams extends SqlParams {

    /**
     * 参数
     */
    private List<Object> params = Lists.newArrayList();


    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
