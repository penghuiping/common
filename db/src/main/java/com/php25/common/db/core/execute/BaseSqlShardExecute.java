package com.php25.common.db.core.execute;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.db.core.JdbcModelRowMapper;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.manager.ModelMeta;
import com.php25.common.db.core.shard.ShardInfo;
import com.php25.common.db.core.sql.DefaultSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/12/23 16:16
 */
public abstract class BaseSqlShardExecute implements SqlExecute {
    protected static final Logger log = LoggerFactory.getLogger(BaseSqlShardExecute.class);

    @Override
    public <T> List<T> select(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        Class<?> resultType = defaultSqlParams.getResultType();
        List<T> list = new ArrayList<>();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();
        ShardInfo shardInfo = defaultSqlParams.getShardInfo();

        if (null != defaultSqlParams.getShardingKeyValue()) {
            //搜索条件中有shardingKey的情况
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            List<T> list0 = shardInfo.getShardingDb().query(targetSql0, paras, new JdbcModelRowMapper<>((Class<T>) resultType));
            list.addAll(list0);
        } else {
            for (int i = 0; i < shardInfo.getDbs().size(); i++) {
                //todo 没处理分页情况
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                List<T> list0 = shardInfo.getDbs().get(i).query(targetSql0, paras, new JdbcModelRowMapper<T>((Class<T>) resultType));
                list.addAll(list0);
            }
        }
        return list;

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
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        List<Map> list = new ArrayList<>();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();
        ShardInfo shardInfo = defaultSqlParams.getShardInfo();
        for (int i = 0; i < shardInfo.getDbs().size(); i++) {
            //todo 没处理分页情况
            //逻辑表名替换成对应的物理表名
            String physicalTableName = physicalTableNames[i];
            String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
            log.info("sql语句为:{}", targetSql0);
            List<Map<String, Object>> list0 = shardInfo.getDbs().get(i).query(targetSql0, paras, new ColumnMapRowMapper());
            list.addAll(list0);
        }
        return list;

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
    public int update(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        ShardInfo shardInfo = defaultSqlParams.getShardInfo();
        //逻辑表名替换成对应的物理表名
        String logicalTableName = modelMeta.getLogicalTableName();
        String physicalTableName = shardInfo.getPhysicTableName();
        String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
        log.info("sql语句为:{}", targetSql0);
        return shardInfo.getShardingDb().update(targetSql, paras);
    }

    @Override
    public int[] updateBatch(SqlParams sqlParams) {
        return new int[0];
    }

    @Override
    public int[] insertBatch(SqlParams sqlParams) {
        return new int[0];
    }

    @Override
    public int delete(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ShardInfo shardInfo = defaultSqlParams.getShardInfo();
        return shardInfo.getShardingDb().update(targetSql, paras);
    }

    @Override
    public long count(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        String logicalTableName = modelMeta.getLogicalTableName();
        ShardInfo shardInfo = defaultSqlParams.getShardInfo();
        Long result = 0L;
        if (null != defaultSqlParams.getShardingKeyValue()) {
            //搜索条件中有shardingKeyValue情况
            String targetSql0 = targetSql.replace(logicalTableName, shardInfo.getPhysicTableName());
            log.info("sql语句为:{}", targetSql0);
            Long result1 = shardInfo.getShardingDb().queryForObject(targetSql0, paras, Long.class);
            if (null != result1) {
                result = result + result1;
            }
        } else {
            //没有则遍历所有分区表
            for (int i = 0; i < shardInfo.getDbs().size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames[i].split("\\.")[1];
                String targetSql0 = targetSql.replace(logicalTableName, physicalTableName);
                log.info("sql语句为:{}", targetSql0);
                Long result1 = shardInfo.getDbs().get(i).queryForObject(targetSql0, paras, Long.class);
                if (null != result1) {
                    result = result + result1;
                }
            }
        }
        return result;
    }
}
