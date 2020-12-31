package com.php25.common.db.core.execute;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.GenerationType;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.manager.ModelMeta;
import com.php25.common.db.core.shard.ShardInfo;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.sql.SingleSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author penghuiping
 * @date 2020/12/24 16:13
 */
public class MysqlSqlShardExecute extends BaseSqlShardExecute {
    private static final Logger log = LoggerFactory.getLogger(MysqlSqlShardExecute.class);


    @Override
    public int insert(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        if (!JdbcModelManager.isShardTable(defaultSqlParams.getClazz())) {
            throw Exceptions.throwImpossibleException();
        }
        Class<?> clazz = defaultSqlParams.getClazz();
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(defaultSqlParams.getClazz());
        //逻辑表名替换成对应的物理表名
        String logicalTableName = modelMeta.getLogicalTableName();
        String[] physicalTableNames = modelMeta.getPhysicalTableNames();
        ShardRule shardRule = JdbcModelManager.getShardRule(sqlParams.getClazz());
        Object shardingKeyValue = JdbcModelManager.getShardingKeyValue(defaultSqlParams.getClazz(), defaultSqlParams.getModel());
        ShardInfo shardInfo = shardRule
                .shard(logicalTableName, physicalTableNames, shardingKeyValue);
        String physicalTableName = shardInfo.getPhysicTableName();
        targetSql = targetSql.replace(logicalTableName, physicalTableName);
        GenerationType generationType = defaultSqlParams.getGenerationType();
        Object model = defaultSqlParams.getModel();

        final String targetSql0 = targetSql;
        log.info("sql语句为:{}", targetSql0);
        try {
            if (GenerationType.IDENTITY.equals(generationType)) {
                //自增操作
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = shardInfo.getShardingDb().update(con -> {
                    PreparedStatement ps = con.prepareStatement(targetSql0, Statement.RETURN_GENERATED_KEYS);
                    int i = 1;
                    for (Object obj : paras) {
                        ps.setObject(i++, obj);
                    }
                    return ps;
                }, keyHolder);
                if (rows <= 0) {
                    throw new DbException("insert 操作失败");
                }
                Field field = JdbcModelManager.getPrimaryKeyField(clazz);
                if (!field.getType().isAssignableFrom(Long.class) && !field.getType().isAssignableFrom(long.class)) {
                    throw new DbException("自增主键必须是Long类型");
                }
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(model, keyHolder.getKey().longValue());
                return rows;
            } else {
                //非自增操作
                int rows = shardInfo.getShardingDb().update(targetSql0, paras);
                if (rows <= 0) {
                    throw new DbException("insert 操作失败");
                }
                return rows;
            }
        } catch (Exception e) {
            throw new DbException("插入操作失败", e);
        }
    }
}
