package com.php25.common.db.core.shard;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 按照时间戳字段进行分区
 *
 * @author penghuiping
 * @date 2020/12/1 15:59
 */
public class ShardRuleTimeBased implements ShardRule {
    @Override
    public ShardInfo shard(List<JdbcTemplate> jdbcTemplates, List<String> physicNames, Object shardingKey) {
        return null;
    }
}
