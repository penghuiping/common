package com.php25.common.db.core.shard;

/**
 * 按照时间戳字段进行分区
 *
 * @author penghuiping
 * @date 2020/12/1 15:59
 */
public class ShardRuleTimeBased implements ShardRule {
    @Override
    public ShardInfo shard(String logicName, String[] physicNames, Object shardingKey) {
        return null;
    }
}
