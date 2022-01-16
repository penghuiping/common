package com.php25.common.db.sql.expression;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 20:40
 */
public interface Expression {

    /**
     * 打印sql
     *
     * @return 打印sql
     */
    String printSql();


    List<Object> params();
}
