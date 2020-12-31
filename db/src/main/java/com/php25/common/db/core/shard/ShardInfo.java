package com.php25.common.db.core.shard;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author penghuiping
 * @date 2020/12/1 15:53
 */
public class ShardInfo {

    private JdbcTemplate shardingDb;

    private String physicTableName;

    public String getPhysicTableName() {
        return physicTableName;
    }

    public void setPhysicTableName(String physicTableName) {
        this.physicTableName = physicTableName;
    }

    public JdbcTemplate getShardingDb() {
        return shardingDb;
    }

    public void setShardingDb(JdbcTemplate shardingDb) {
        this.shardingDb = shardingDb;
    }
}
