package com.php25.common.db.core.execute;

import com.php25.common.core.util.AssertUtil;
import com.php25.common.db.core.JdbcModelRowMapper;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.manager.ModelMeta;
import com.php25.common.db.core.shard.ShardInfo;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.sql.BatchSqlParams;
import com.php25.common.db.core.sql.SingleSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/12/23 16:16
 */
public abstract class BaseSqlShardExecute implements ShardSqlExecute {
    protected static final Logger log = LoggerFactory.getLogger(BaseSqlShardExecute.class);

    @Override
    public <T> List<T> select(SqlParams sqlParams) {
        return this.select(sqlParams, null);
    }

    @Override
    public <M> M single(SqlParams sqlParams) {
        List<M> list = select(sqlParams);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Map> mapSelect(SqlParams sqlParams) {
        return this.mapSelect(sqlParams, null);
    }

    @Override
    public Map mapSingle(SqlParams sqlParams) {
        List<Map> list = mapSelect(sqlParams);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public int[] updateBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            List<Object[]> batchParams = batchSqlParams.getBatchParams();
            List<Object> models = batchSqlParams.getModels();
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            String logicTableName = JdbcModelManager.getLogicalTableName(sqlParams.getClazz());
            String[] physicalTableNames = JdbcModelManager.getPhysicalTableName(sqlParams.getClazz());

            List<ImmutablePair<ShardInfo, List<Object[]>>> pairs = new ArrayList<>();
            for (int j = 0; j < physicalTableNames.length; j++) {
                String physicalTableName = physicalTableNames[j].split("\\.")[1];
                ShardInfo shardInfo = null;
                List<Object[]> batchParams0 = new ArrayList<>();
                for (int i = 0; i < models.size(); i++) {
                    Object model = models.get(i);
                    Object[] params = batchParams.get(i);
                    Object shardingKeyValue = JdbcModelManager.getShardingKeyValue(sqlParams.getClazz(), model);
                    ShardInfo shardInfo0 = shardRule.shard(logicTableName, physicalTableNames, shardingKeyValue);
                    if (physicalTableName.equals(shardInfo0.getPhysicTableName())) {
                        if (shardInfo == null) {
                            shardInfo = shardInfo0;
                        }
                        batchParams0.add(params);
                    }
                }
                ImmutablePair<ShardInfo, List<Object[]>> pair = new ImmutablePair<>(shardInfo, batchParams0);
                pairs.add(pair);
            }

            for (int i = 0; i < pairs.size(); i++) {
                ImmutablePair<ShardInfo, List<Object[]>> pair = pairs.get(i);
                ShardInfo shardInfo = pair.getLeft();
                List<Object[]> batchParams1 = pair.getRight();
                String targetSql = batchSqlParams.getSql();
                String targetSql0 = targetSql.replace(logicTableName, shardInfo.getPhysicTableName());
                log.info("sql语句为:{}", targetSql0);
                shardInfo.getShardingDb().batchUpdate(targetSql0, batchParams1);
            }
        } catch (Exception e) {
            throw new DbException("批量更新操作失败", e);
        }
        return new int[0];
    }

    @Override
    public int delete(SqlParams sqlParams) {
        return this.delete(sqlParams, null);
    }

    @Override
    public long count(SqlParams sqlParams) {
        return this.count(sqlParams, null);
    }


    @Override
    public <T> List<T> select(SqlParams sqlParams, Object shardingKeyValue) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw new DbException("实体类不支持分片操作，注意是否存在@TableShard注解");
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        Class<?> resultType = defaultSqlParams.getResultType();
        List<T> list = new ArrayList<>();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();

        if (null != shardingKeyValue) {
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            ShardInfo shardInfo = shardRule.shard(logicalTableName, physicalTableNames, shardingKeyValue);
            //搜索条件中有shardingKey的情况
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            List<T> list0 = shardInfo.getShardingDb().query(targetSql0, paras, new JdbcModelRowMapper<>((Class<T>) resultType));
            list.addAll(list0);
        } else {
            List<JdbcTemplate> dbs = JdbcModelManager.getAllShardingDbs(sqlParams.getClazz());
            for (int i = 0; i < dbs.size(); i++) {
                //todo 没处理分页情况
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                List<T> list0 = dbs.get(i).query(targetSql0, paras, new JdbcModelRowMapper<T>((Class<T>) resultType));
                list.addAll(list0);
            }
        }
        return list;
    }

    @Override
    public <M> M single(SqlParams sqlParams, Object shardingKeyValue) {
        List<M> list = select(sqlParams, shardingKeyValue);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Map> mapSelect(SqlParams sqlParams, Object shardingKeyValue) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw new DbException("实体类不支持分片操作，注意是否存在@TableShard注解");
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        List<Map> list = new ArrayList<>();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();

        if (null != shardingKeyValue) {
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            ShardInfo shardInfo = shardRule.shard(logicalTableName, physicalTableNames, shardingKeyValue);
            //搜索条件中有shardingKey的情况
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            List<Map<String, Object>> list0 = shardInfo.getShardingDb().query(targetSql0, paras, new ColumnMapRowMapper());
            list.addAll(list0);
        } else {
            List<JdbcTemplate> dbs = JdbcModelManager.getAllShardingDbs(sqlParams.getClazz());
            for (int i = 0; i < dbs.size(); i++) {
                //todo 没处理分页情况
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                List<Map<String, Object>> list0 = dbs.get(i).query(targetSql0, paras, new ColumnMapRowMapper());
                list.addAll(list0);
            }
        }
        return list;
    }

    @Override
    public Map mapSingle(SqlParams sqlParams, Object shardingKeyValue) {
        List<Map> list = mapSelect(sqlParams, shardingKeyValue);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public int update(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw new DbException("实体类不支持分片操作，注意是否存在@TableShard注解");
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String logicalTableName = modelMeta.getLogicalTableName();
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
        Object shardingKeyValue = JdbcModelManager.getShardingKeyValue(defaultSqlParams.getClazz(), defaultSqlParams.getModel());
        if (null != shardingKeyValue) {
            //存在分区键
            ShardInfo shardInfo = shardRule
                    .shard(logicalTableName, physicalTableNames, shardingKeyValue);
            String physicalTableName = shardInfo.getPhysicTableName();
            //逻辑表名替换成对应的物理表名
            String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
            log.info("sql语句为:{}", targetSql0);
            return shardInfo.getShardingDb().update(targetSql0, paras);
        } else {
            List<JdbcTemplate> dbs = JdbcModelManager.getAllShardingDbs(defaultSqlParams.getClazz());
            for (int i = 0; i < dbs.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                int result0 = dbs.get(i).update(targetSql0, paras);
                if (result0 > 0) {
                    return result0;
                }
            }
        }
        return 0;
    }

    @Override
    public int delete(SqlParams sqlParams, Object shardingKeyValue) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw new DbException("实体类不支持分片操作，注意是否存在@TableShard注解");
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();
        int result = 0;
        if (null != shardingKeyValue) {
            //存在分区键
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            ShardInfo shardInfo = shardRule
                    .shard(logicalTableName, physicalTableNames, shardingKeyValue);
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            result = result + shardInfo.getShardingDb().update(targetSql0, paras);
        } else {
            //不存在分区键
            List<JdbcTemplate> dbs = JdbcModelManager.getAllShardingDbs(defaultSqlParams.getClazz());
            for (int i = 0; i < dbs.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                int result0 = dbs.get(i).update(targetSql0, paras);
                result = result + result0;
            }
        }
        return result;
    }

    @Override
    public long count(SqlParams sqlParams, Object shardingKeyValue) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw new DbException("实体类不支持分片操作，注意是否存在@TableShard注解");
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();
        long result = 0L;
        if (null != shardingKeyValue) {
            //搜索条件中有shardingKey的情况
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            ShardInfo shardInfo = shardRule
                    .shard(logicalTableName, physicalTableNames, shardingKeyValue);
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            Long result1 = shardInfo.getShardingDb().queryForObject(targetSql0, paras, Long.class);
            if (null != result1) {
                result = result + result1;
            }
        } else {
            //没有则遍历所有分区表
            List<JdbcTemplate> dbs = JdbcModelManager.getAllShardingDbs(sqlParams.getClazz());
            for (int i = 0; i < dbs.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                Long result1 = dbs.get(i).queryForObject(targetSql0, paras, Long.class);
                if (null != result1) {
                    result = result + result1;
                }
            }
        }
        return result;
    }

    @Override
    public int[] insertBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            List<Object[]> batchParams = batchSqlParams.getBatchParams();
            List<Object> models = batchSqlParams.getModels();
            ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
            String logicTableName = JdbcModelManager.getLogicalTableName(sqlParams.getClazz());
            String[] physicalTableNames = JdbcModelManager.getPhysicalTableName(sqlParams.getClazz());

            List<ImmutablePair<ShardInfo, List<Object[]>>> pairs = new ArrayList<>();
            for (int j = 0; j < physicalTableNames.length; j++) {
                String physicalTableName = physicalTableNames[j].split("\\.")[1];
                ShardInfo shardInfo = null;
                List<Object[]> batchParams0 = new ArrayList<>();
                for (int i = 0; i < models.size(); i++) {
                    Object model = models.get(i);
                    Object[] params = batchParams.get(i);
                    Object shardingKeyValue = JdbcModelManager.getShardingKeyValue(sqlParams.getClazz(), model);
                    ShardInfo shardInfo0 = shardRule.shard(logicTableName, physicalTableNames, shardingKeyValue);
                    if (physicalTableName.equals(shardInfo0.getPhysicTableName())) {
                        if (shardInfo == null) {
                            shardInfo = shardInfo0;
                        }
                        batchParams0.add(params);
                    }
                }
                ImmutablePair<ShardInfo, List<Object[]>> pair = new ImmutablePair<>(shardInfo, batchParams0);
                pairs.add(pair);
            }

            for (int i = 0; i < pairs.size(); i++) {
                ImmutablePair<ShardInfo, List<Object[]>> pair = pairs.get(i);
                ShardInfo shardInfo = pair.getLeft();
                AssertUtil.notNull(shardInfo, "对于insert操作必须指定分区键值");
                List<Object[]> batchParams1 = pair.getRight();
                String targetSql = batchSqlParams.getSql();
                String targetSql0 = targetSql.replace(logicTableName, shardInfo.getPhysicTableName());
                log.info("sql语句为:{}", targetSql0);
                shardInfo.getShardingDb().batchUpdate(targetSql0, batchParams1);
            }
        } catch (Exception e) {
            throw new DbException("插入操作失败", e);
        }
        return new int[0];
    }
}
