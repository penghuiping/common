package com.php25.common.db.core.shard;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/1/14 09:01
 */
public class ShardTableInfo {

    private List<String> physicalTableNames;

    private Class<?> modelClass;

    private ShardRule shardRule;

    private Object shardingKeyValue;

    private ShardTableInfo() {
    }

    public static ShardTableInfo of(Class<?> modelClass, List<String> physicalTableNames) {
        ShardTableInfo shardTableInfo = new ShardTableInfo();
        shardTableInfo.modelClass = modelClass;
        shardTableInfo.physicalTableNames = physicalTableNames;
        return shardTableInfo;
    }

    public ShardTableInfo shardRule(ShardRule shardRule, Object shardingKeyValue) {
        this.shardRule = shardRule;
        this.shardingKeyValue = shardingKeyValue;
        return this;
    }

    public List<String> getPhysicalTableNames() {
        return physicalTableNames;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public ShardRule getShardRule() {
        return shardRule;
    }

    public Object getShardingKeyValue() {
        return shardingKeyValue;
    }
}
