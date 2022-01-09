package com.php25.common.db.core.sql.context;

/**
 * @author penghuiping
 * @date 2022/1/9 21:51
 */
public interface SqlContextAction {

    /**
     * 获取sqlContext
     *
     * @return sqlContext sql上下文
     */
    SqlContext getSqlContext();

    /**
     * 设置sqlContext
     *
     * @param sqlContext sql上下文
     */
    void setSqlContext(SqlContext sqlContext);
}
