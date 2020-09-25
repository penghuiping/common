package com.php25.common.db.repository.shard;

import com.php25.common.db.Db;

/**
 * @author penghuiping
 * @date 2020/9/24 14:26
 */
public interface TransactionCallback<T> {

    /**
     * 事务处理方法
     *
     * @return 事务处理方法返回结果
     */
    T doInTransaction();

    /**
     * 获取对应的需要写入的数据库
     *
     * @return 需要写入的数据库
     */
    Db getDb();
}
