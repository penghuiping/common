package com.php25.common.db.core.shard;

import com.php25.common.db.Db;

/**
 * @author penghuiping
 * @date 2020/12/1 15:53
 */
public class ShardInfo {

    private Db db;

    private String physicTableName;

    public Db getDb() {
        return db;
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public String getPhysicTableName() {
        return physicTableName;
    }

    public void setPhysicTableName(String physicTableName) {
        this.physicTableName = physicTableName;
    }
}
