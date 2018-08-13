package com.php25.common.sql;


public interface QueryOther {

    /**
     * having子句
     *
     * @param condition
     * @return
     */
    Query having(QueryCondition condition);

    /***
     * groupBy 子句
     * @param column
     * @return
     */
    Query groupBy(String column);

    /***
     * orderBy 子句
     * 例如 orderBy id desc,user_id asc
     * @param orderBy
     * @return
     */
    Query orderBy(String orderBy);

    Query asc(String column);

    Query desc(String column);

    /***
     * limit 子句
     * @param startRow 开始行数（包含）
     * @param pageSize
     * @return
     */
    Query limit(long startRow, long pageSize);

}
