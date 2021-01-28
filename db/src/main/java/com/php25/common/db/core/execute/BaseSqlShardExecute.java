package com.php25.common.db.core.execute;

import com.google.common.collect.ImmutableMap;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.Constants;
import com.php25.common.db.core.JdbcModelRowMapper;
import com.php25.common.db.core.shard.ShardInfo;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.shard.ShardTableInfo;
import com.php25.common.db.core.sql.SingleSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.util.StringFormatter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/12/23 16:16
 */
public abstract class BaseSqlShardExecute implements ShardSqlExecute {
    protected static final Logger log = LoggerFactory.getLogger(BaseSqlShardExecute.class);

    protected Map<String, ShardTableInfo> shardTableInfos = new HashMap<>(16);

    protected List<JdbcTemplate> jdbcTemplates;

    public BaseSqlShardExecute with(List<JdbcTemplate> jdbcTemplates) {
        this.jdbcTemplates = jdbcTemplates;
        return this;
    }

    public BaseSqlShardExecute with(ShardTableInfo shardTableInfo) {
        shardTableInfos.put(shardTableInfo.getModelClass().getName(), shardTableInfo);
        return this;
    }

    @Override
    public <T> List<T> select(SqlParams sqlParams) {
        //收集参数字段
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        Class<?> resultType = defaultSqlParams.getResultType();
        List<T> list = new ArrayList<>();
        log.debug("替换前sql语句为:{}", targetSql);

        ShardTableInfo shardTableInfo = this.shardTableInfos.get(defaultSqlParams.getClazz().getName());
        List<String> physicalTableNames = shardTableInfo.getPhysicalTableNames();
        ShardRule shardRule = shardTableInfo.getShardRule();
        Object shardingKeyValue = shardTableInfo.getShardingKeyValue();

        if (null != shardingKeyValue && null != shardRule) {
            ShardInfo shardInfo = shardRule.shard(jdbcTemplates, physicalTableNames, shardingKeyValue);
            //搜索条件中有shardingKey的情况
            String targetSql0 = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName()
                            , shardInfo.getPhysicTableName()));
            log.info("sql语句为:{}", targetSql0);
            List<T> list0 = shardInfo.getShardingDb().query(targetSql0, paras, new JdbcModelRowMapper<>((Class<T>) resultType));
            list.addAll(list0);
        } else {
            //无shardingKey
            if (sqlParams.getStartRow() >= 0) {
                //此实现:处理分页逻辑,随着分页深度越深,性能越差
                targetSql = new StringFormatter(targetSql)
                        .format(ImmutableMap.of(
                                Constants.START_ROW, 0, Constants.PAGE_SIZE,
                                sqlParams.getStartRow() + sqlParams.getPageSize()));
            }

            for (int i = 0; i < jdbcTemplates.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String targetSql0 = targetSql;
                for (Map.Entry<String, ShardTableInfo> entry : this.shardTableInfos.entrySet()) {
                    ShardTableInfo shardTableInfo1 = entry.getValue();
                    List<String> physicalTableNames1 = shardTableInfo1.getPhysicalTableNames();
                    Class<?> clazz = shardTableInfo1.getModelClass();
                    String physicalTableName1 = physicalTableNames1.get(i);
                    targetSql0 = new StringFormatter(targetSql0)
                            .format(ImmutableMap.of(clazz.getSimpleName(), physicalTableName1));
                }
                log.info("sql语句为:{}", targetSql0);
                List<T> list0 = jdbcTemplates.get(i).query(targetSql0, paras, new JdbcModelRowMapper<T>((Class<T>) resultType));
                list.addAll(list0);
            }

            //多表中的子表已经完成局部order by排序
            //需要完成全局order by排序操作
            list = sortOrderBy(sqlParams, list);

            //分页处理，丢弃不需要的数据
            if (sqlParams.getStartRow() >= 0) {
                list = list.subList(sqlParams.getStartRow(), sqlParams.getStartRow() + sqlParams.getPageSize());
            }
        }
        return list;
    }

    //order 排序
    private <T> List<T> sortOrderBy(SqlParams sqlParams, List<T> list) {
        final List<Pair<String, String>> pairs = sqlParams.getOrders();
        if (null != pairs && pairs.size() > 0) {
            list = list.stream().sorted((o1, o2) -> {
                int res = 0;
                for (int i = 0; i < pairs.size(); i++) {
                    try {
                        Pair<String, String> pair = pairs.get(i);
                        //类属性名
                        String left = pair.getLeft();
                        //asc,desc
                        String right = pair.getRight();
                        Object obj1 = ReflectUtil.getMethod(sqlParams.getResultType(), "get" + StringUtil.capitalizeFirstLetter(left)).invoke(o1);
                        Object obj2 = ReflectUtil.getMethod(sqlParams.getResultType(), "get" + StringUtil.capitalizeFirstLetter(left)).invoke(o2);
                        if (obj1 instanceof Comparable && obj2 instanceof Comparable) {
                            Comparable comparable1 = (Comparable) obj1;
                            Comparable comparable2 = (Comparable) obj2;
                            res = comparable1.compareTo(comparable2);
                            if (res != 0) {
                                if ("DESC".equals(right.toUpperCase())) {
                                    res = -res;
                                }
                                break;
                            }
                        } else {
                            throw new DbException("排序属性字段必须实现Comparable接口");
                        }
                    } catch (Exception e) {
                        throw new DbException("查询结果排序出错", e);
                    }
                }
                return res;
            }).collect(Collectors.toList());
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
        //收集参数
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        List<Map> list = new ArrayList<>();
        log.debug("替换前sql语句为:{}", targetSql);

        ShardTableInfo shardTableInfo = shardTableInfos.get(defaultSqlParams.getClazz().getName());
        List<String> physicalTableNames = shardTableInfo.getPhysicalTableNames();
        Object shardingKeyValue = shardTableInfo.getShardingKeyValue();
        ShardRule shardRule = shardTableInfo.getShardRule();

        if (null != shardingKeyValue && null != shardRule) {
            ShardInfo shardInfo = shardRule.shard(jdbcTemplates, physicalTableNames, shardingKeyValue);
            //搜索条件中有shardingKey的情况
            String targetSql0 = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), shardInfo.getPhysicTableName()));
            log.info("sql语句为:{}", targetSql0);
            List<Map<String, Object>> list0 = shardInfo.getShardingDb().query(targetSql0, paras, new ColumnMapRowMapper());
            list.addAll(list0);
        } else {
            if (sqlParams.getStartRow() >= 0) {
                //此实现:处理分页逻辑,随着分页深度越深,性能越差
                targetSql = new StringFormatter(targetSql).format(ImmutableMap.of(Constants.START_ROW, 0,
                        Constants.PAGE_SIZE, sqlParams.getStartRow() + sqlParams.getPageSize()));
            }

            for (int i = 0; i < jdbcTemplates.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String targetSql0 = targetSql;
                for (Map.Entry<String, ShardTableInfo> entry : this.shardTableInfos.entrySet()) {
                    ShardTableInfo shardTableInfo1 = entry.getValue();
                    List<String> physicalTableNames1 = shardTableInfo1.getPhysicalTableNames();
                    String physicalTableName1 = physicalTableNames1.get(i);
                    targetSql0 = new StringFormatter(targetSql0)
                            .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), physicalTableName1));
                }
                log.info("sql语句为:{}", targetSql0);
                List<Map<String, Object>> list0 = jdbcTemplates.get(i).query(targetSql0, paras, new ColumnMapRowMapper());
                list.addAll(list0);
            }

            //多表中的子表已经完成局部order by排序
            //需要完成全局order by排序操作
            list = sortOrderBy(sqlParams, list);

            //分页处理，丢弃不需要的数据
            if (sqlParams.getStartRow() >= 0) {
                list = list.subList(sqlParams.getStartRow(), sqlParams.getStartRow() + sqlParams.getPageSize());
            }
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
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        log.debug("替换前sql语句为:{}", targetSql);
        Object[] paras = defaultSqlParams.getParams().toArray();
        ShardTableInfo shardTableInfo = this.shardTableInfos.get(defaultSqlParams.getClazz().getName());
        List<String> physicalTableNames = shardTableInfo.getPhysicalTableNames();
        ShardRule shardRule = shardTableInfo.getShardRule();
        Object shardingKeyValue = shardTableInfo.getShardingKeyValue();
        if (null != shardingKeyValue && null != shardRule) {
            //存在分区键
            ShardInfo shardInfo = shardRule
                    .shard(jdbcTemplates, physicalTableNames, shardingKeyValue);
            String physicalTableName = shardInfo.getPhysicTableName();
            //逻辑表名替换成对应的物理表名
            String targetSql0 = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), physicalTableName));
            log.info("sql语句为:{}", targetSql0);
            return shardInfo.getShardingDb().update(targetSql0, paras);
        } else {
            for (int i = 0; i < jdbcTemplates.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames.get(i);
                String targetSql0 = new StringFormatter(targetSql)
                        .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), physicalTableName));
                log.info("sql语句为:{}", targetSql0);
                int result0 = jdbcTemplates.get(i).update(targetSql0, paras);
                if (result0 > 0) {
                    return result0;
                }
            }
        }
        return 0;
    }

    @Override
    public int delete(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        log.debug("替换前sql语句为:{}", targetSql);
        Object[] paras = defaultSqlParams.getParams().toArray();
        ShardTableInfo shardTableInfo = shardTableInfos.get(defaultSqlParams.getClazz().getName());
        List<String> physicalTableNames = shardTableInfo.getPhysicalTableNames();
        Object shardingKeyValue = shardTableInfo.getShardingKeyValue();
        ShardRule shardRule = shardTableInfo.getShardRule();
        int result = 0;
        if (null != shardingKeyValue && null != shardRule) {
            //存在分区键
            ShardInfo shardInfo = shardRule
                    .shard(jdbcTemplates, physicalTableNames, shardingKeyValue);
            String targetSql0 = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), shardInfo.getPhysicTableName()));
            log.info("sql语句为:{}", targetSql0);
            result = result + shardInfo.getShardingDb().update(targetSql0, paras);
        } else {
            //不存在分区键
            for (int i = 0; i < jdbcTemplates.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames.get(i);
                String targetSql0 = new StringFormatter(targetSql)
                        .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), physicalTableName));
                log.info("sql语句为:{}", targetSql0);
                int result0 = jdbcTemplates.get(i).update(targetSql0, paras);
                result = result + result0;
            }
        }
        return result;
    }

    @Override
    public long count(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        log.debug("替换前sql语句为:{}", targetSql);
        Object[] paras = defaultSqlParams.getParams().toArray();
        ShardTableInfo shardTableInfo = shardTableInfos.get(defaultSqlParams.getClazz().getName());
        List<String> physicalTableNames = shardTableInfo.getPhysicalTableNames();
        Object shardingKeyValue = shardTableInfo.getShardingKeyValue();
        ShardRule shardRule = shardTableInfo.getShardRule();
        long result = 0L;
        if (null != shardingKeyValue && null != shardRule) {
            //搜索条件中有shardingKey的情况
            ShardInfo shardInfo = shardRule
                    .shard(jdbcTemplates, physicalTableNames, shardingKeyValue);
            String targetSql0 = new StringFormatter(targetSql)
                    .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), shardInfo.getPhysicTableName()));
            log.info("sql语句为:{}", targetSql0);
            Long result1 = shardInfo.getShardingDb().queryForObject(targetSql0, paras, Long.class);
            if (null != result1) {
                result = result + result1;
            }
        } else {
            //没有则遍历所有分区表
            for (int i = 0; i < jdbcTemplates.size(); i++) {
                //逻辑表名替换成对应的物理表名
                String physicalTableName = physicalTableNames.get(i);
                String targetSql0 = new StringFormatter(targetSql)
                        .format(ImmutableMap.of(defaultSqlParams.getClazz().getSimpleName(), physicalTableName));
                log.info("sql语句为:{}", targetSql0);
                Long result1 = jdbcTemplates.get(i).queryForObject(targetSql0, paras, Long.class);
                if (null != result1) {
                    result = result + result1;
                }
            }
        }
        return result;
    }
}
