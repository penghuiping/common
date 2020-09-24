package com.php25.common.db.repository.shard;

import com.php25.common.db.Db;

/**
 * @author penghuiping
 * @date 2020/9/24 14:26
 */
public interface TransactionCallback<T> {

    T doInTransaction();


    Db getDb();
}
