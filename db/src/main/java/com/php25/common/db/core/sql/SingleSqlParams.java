package com.php25.common.db.core.sql;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/11 15:24
 */
public class SingleSqlParams extends SqlParams {

    /**
     * 实体对象
     */
    private Object model;

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

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
