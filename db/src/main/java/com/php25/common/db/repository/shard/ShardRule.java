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
     * @param dbs         所有的需要分片的db
     * @param shardingKey 分片键
     * @return 返回
     */
    Db shard(List<Db> dbs, Object shardingKey);
}
