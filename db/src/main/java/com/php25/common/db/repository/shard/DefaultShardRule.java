package com.php25.common.db.repository.shard;

import com.php25.common.db.Db;

import java.util.List;

/**
 * 默认的shard规则，shardingKey的hash值对数据库数进行取模运算
 * 计算出应该使用哪个数据库
 *
 * @author penghuiping
 * @date 2020/9/25 11:03
 */
public class DefaultShardRule implements ShardRule {

    @Override
    public Db shard(List<Db> dbs, Object shardingKey) {
        if (shardingKey instanceof Long || shardingKey instanceof Integer) {
            long value = Long.parseLong(shardingKey.toString()) % dbs.size();
            return dbs.get((int) value);
        } else if (shardingKey instanceof String) {
            char[] values = shardingKey.toString().toCharArray();
            int v = 0;
            for (char c : values) {
                v = v + c;
            }
            return dbs.get(v % dbs.size());
        }
        return dbs.get(shardingKey.hashCode() % dbs.size());
    }
}
