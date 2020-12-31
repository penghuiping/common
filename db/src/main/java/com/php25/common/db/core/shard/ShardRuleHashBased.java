package com.php25.common.db.core.shard;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.db.Db;

/**
 * 使用通过shardingKey的hashcode取模方式进行计算分区信息
 *
 * @author penghuiping
 * @date 2020/12/1 15:58
 */
public class ShardRuleHashBased implements ShardRule {
    @Override
    public ShardInfo shard(String logicName, String[] physicNames, Object shardingKey) {
        ShardInfo shardInfo = new ShardInfo();
        if (null != shardingKey) {
            int size = physicNames.length;
            int index = -1;
            if (shardingKey instanceof Long || shardingKey instanceof Integer) {
                long value = Long.parseLong(shardingKey.toString()) % size;
                index = (int) value;
            } else if (shardingKey instanceof String) {
                char[] values = shardingKey.toString().toCharArray();
                int v = 0;
                for (char c : values) {
                    v = v + c;
                }
                index = v % size;
            }

            if (index < 0) {
                index = shardingKey.hashCode() % size;
            }

            String physicName = physicNames[index];
            String[] physicNameSplit = physicName.split("\\.");
            String dbBeanName = physicNameSplit[0];
            String tablePhysicName = physicNameSplit[1];
            Db db = (Db) SpringContextHolder.getApplicationContext().getBean(dbBeanName);
            shardInfo.setShardingDb(db.getJdbcPair().getJdbcTemplate());
            shardInfo.setPhysicTableName(tablePhysicName);
        }
        return shardInfo;
    }
}
