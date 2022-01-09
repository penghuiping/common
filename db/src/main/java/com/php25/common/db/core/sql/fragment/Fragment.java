package com.php25.common.db.core.sql.fragment;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/1 21:15
 */
public interface Fragment {
    /**
     * 打印sql
     *
     * @return 打印sql
     */
    String printSql();


    List<Object> params();

}
