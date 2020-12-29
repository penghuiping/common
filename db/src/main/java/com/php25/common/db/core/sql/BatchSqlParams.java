package com.php25.common.db.core.sql;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/11 15:25
 */
public class BatchSqlParams extends SqlParams {

    /**
     * 参数,用于批量sql操作
     */
    private List<Object[]> batchParams;

    public List<Object[]> getBatchParams() {
        return batchParams;
    }

    public void setBatchParams(List<Object[]> batchParams) {
        this.batchParams = batchParams;
    }
}
