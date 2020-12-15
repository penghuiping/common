package com.php25.common.db.core.shard;

/**
 * @author penghuiping
 * @date 2020/12/1 15:47
 */
public interface ShardRule {

    /**
     * 计算分区信息
     *
     * @param logicName   逻辑表名
     * @param physicNames 物理表名 例如: ${Db的bean名}.${物理表名}
     * @param shardingKey 分区键
     * @return shard引用
     */
    ShardInfo shard(String logicName, String[] physicNames, Object shardingKey);
}
