package com.php25.common.db.core.shard;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/1 15:47
 */
public interface ShardRule {

    /**
     * 计算分区信息
     *
     * @param jdbcTemplates    物理表名对应jdbc
     * @param physicNames      物理表名
     * @param shardingKeyValue 分区键值
     * @return shard引用
     */
    ShardInfo shard(List<JdbcTemplate> jdbcTemplates, List<String> physicNames, Object shardingKeyValue);
}
