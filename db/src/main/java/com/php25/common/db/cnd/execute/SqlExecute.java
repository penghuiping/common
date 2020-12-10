package com.php25.common.db.cnd.execute;

import com.php25.common.db.cnd.sql.SqlParams;

import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/12/4 17:47
 */
public interface SqlExecute {

    <T> List<T> select(SqlParams sqlParams);

    <M> M single(SqlParams sqlParams);

    List<Map> mapSelect(SqlParams sqlParams);

    Map mapSingle(SqlParams sqlParams);

    int update(SqlParams sqlParams);

    int[] updateBatch(SqlParams sqlParams);

    int insert(SqlParams sqlParams);

    int[] insertBatch(SqlParams sqlParams);

    int delete(SqlParams sqlParams);

    long count(SqlParams sqlParams);
}
