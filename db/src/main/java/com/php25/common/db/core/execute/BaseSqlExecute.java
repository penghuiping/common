package com.php25.common.db.core.execute;

import com.google.common.collect.ImmutableMap;
import com.php25.common.db.core.Constants;
import com.php25.common.db.core.JdbcModelRowMapper;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.BatchSqlParams;
import com.php25.common.db.core.sql.SingleSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.util.StringFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/12/4 22:33
 */
public abstract class BaseSqlExecute implements SqlExecute {
    protected static final Logger log = LoggerFactory.getLogger(BaseSqlExecute.class);

    protected JdbcTemplate jdbcTemplate;

    public BaseSqlExecute with(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    @Override
    public <T> List<T> select(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        Class<?> resultType = defaultSqlParams.getResultType();
        Class<?> clazz = defaultSqlParams.getClazz();
        List<T> list = null;
        log.debug("替换前sql语句为:{}", targetSql);
        if (sqlParams.getStartRow() >= 0) {
            targetSql = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(Constants.START_ROW, sqlParams.getStartRow(),
                            Constants.PAGE_SIZE, sqlParams.getPageSize()));
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put(clazz.getSimpleName(), JdbcModelManager.getLogicalTableName(clazz));
        //join格式化
        List<Class<?>> joinClazz = defaultSqlParams.getJoinClazz();
        if (null != joinClazz && joinClazz.size() > 0) {
            for (Class<?> cls : joinClazz) {
                map.put(cls.getSimpleName(), JdbcModelManager.getLogicalTableName(cls));
            }
        }
        targetSql = new StringFormatter(targetSql).format(map);


        log.info("sql语句为:{}", targetSql);
        if (resultType.isAssignableFrom(Map.class)) {
            list = (List<T>) this.jdbcTemplate.query(targetSql, paras, new ColumnMapRowMapper());
        } else {
            list = this.jdbcTemplate.query(targetSql, paras, new JdbcModelRowMapper<T>((Class<T>) resultType));
        }
        return list;
    }

    @Override
    public List<Map> mapSelect(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        defaultSqlParams.setModel(Map.class);
        return this.select(sqlParams);
    }


    @Override
    public Map mapSingle(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        defaultSqlParams.setModel(Map.class);
        List<Map> result = this.select(sqlParams);
        if (null != result && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public <M> M single(SqlParams sqlParams) {
        List<M> result = this.select(sqlParams);
        if (null != result && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }


    @Override
    public int update(SqlParams sqlParams) {
        try {
            SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
            String targetSql = defaultSqlParams.getSql();
            log.debug("替换前sql语句为:{}", targetSql);
            targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(
                    sqlParams.getClazz().getSimpleName(),
                    JdbcModelManager.getLogicalTableName(sqlParams.getClazz())));
            log.info("sql语句为:{}", targetSql);
            return this.jdbcTemplate.update(targetSql, defaultSqlParams.getParams().toArray());
        } catch (Exception e) {
            throw new DbException("更新操作失败", e);
        }
    }

    @Override
    public int[] updateBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            String targetSql = batchSqlParams.getSql();
            log.debug("替换前sql语句为:{}", targetSql);
            targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(
                    sqlParams.getClazz().getSimpleName(),
                    JdbcModelManager.getLogicalTableName(sqlParams.getClazz())));
            log.info("sql语句为:{}", targetSql);
            return this.jdbcTemplate.batchUpdate(targetSql, batchSqlParams.getBatchParams());
        } catch (Exception e) {
            throw new DbException("批量更新操作失败", e);
        }
    }

    @Override
    public int[] insertBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            String targetSql = batchSqlParams.getSql();
            log.debug("替换前sql语句为:{}", targetSql);
            targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(
                    sqlParams.getClazz().getSimpleName(),
                    JdbcModelManager.getLogicalTableName(sqlParams.getClazz())));
            log.info("sql语句为:{}", targetSql);
            return this.jdbcTemplate.batchUpdate(targetSql, batchSqlParams.getBatchParams());
        } catch (Exception e) {
            throw new DbException("插入操作失败", e);
        }
    }

    @Override
    public int delete(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        log.debug("替换前sql语句为:{}", targetSql);
        targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(
                sqlParams.getClazz().getSimpleName(),
                JdbcModelManager.getLogicalTableName(sqlParams.getClazz())));
        log.info("sql语句为:{}", targetSql);
        return this.jdbcTemplate.update(targetSql, defaultSqlParams.getParams().toArray());
    }

    @Override
    public long count(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        log.debug("替换前sql语句为:{}", targetSql);
        targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(
                sqlParams.getClazz().getSimpleName(),
                JdbcModelManager.getLogicalTableName(sqlParams.getClazz())));
        log.info("sql语句为:{}", targetSql);
        Long result = this.jdbcTemplate.queryForObject(targetSql, defaultSqlParams.getParams().toArray(), Long.class);
        if (null == result) {
            result = -1L;
        }
        return result;
    }
}
