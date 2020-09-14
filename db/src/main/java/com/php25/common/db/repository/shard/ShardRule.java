package com.php25.common.db.repository.shard;

import com.php25.common.db.Db;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/9/9 13:26
 */
public interface ShardRule {

    /**
     * 根据主键值进行shard
     *
     * @param dbs
     * @param pkValue
     * @return
     */
    Db shardPrimaryKey(List<Db> dbs, Object pkValue);
}
