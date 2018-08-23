package com.php25.common.jdbc;

/**
 * sql查询条件 group、having、order、limit
 *
 * @author penghuiping
 * @date 2018-0-23
 */
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

    /**
     * order by {column} asc
     *
     * @param column
     * @return
     */
    Query asc(String column);

    /**
     * order by {column} desc
     *
     * @param column
     * @return
     */
    Query desc(String column);

    /***
     * limit 子句
     * @param startRow 开始行数（包含）
     * @param pageSize
     * @return
     */
    Query limit(long startRow, long pageSize);

}
