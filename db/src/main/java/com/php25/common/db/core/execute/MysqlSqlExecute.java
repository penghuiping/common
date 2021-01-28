package com.php25.common.db.core.execute;

import com.google.common.collect.ImmutableMap;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.GenerationType;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.SingleSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.util.StringFormatter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/7 14:05
 */
public class MysqlSqlExecute extends BaseSqlExecute {

    @Override
    public int insert(SqlParams sqlParams) {
        SingleSqlParams defaultSqlParams = (SingleSqlParams) sqlParams;
        Class<?> clazz = defaultSqlParams.getClazz();
        String targetSql = defaultSqlParams.getSql();
        List<Object> params = defaultSqlParams.getParams();
        GenerationType generationType = defaultSqlParams.getGenerationType();
        Object model = defaultSqlParams.getModel();
        log.debug("替换前sql语句为:{}", targetSql);
        String targetSql0 = new StringFormatter(targetSql).format(ImmutableMap.of(clazz.getSimpleName(), JdbcModelManager.getLogicalTableName(clazz)));
        log.info("sql语句为:{}", targetSql0);
        try {
            if (GenerationType.IDENTITY.equals(generationType)) {
                //自增操作
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = this.jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(targetSql0, Statement.RETURN_GENERATED_KEYS);
                    int i = 1;
                    for (Object obj : params.toArray()) {
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
                int rows = this.jdbcTemplate.update(targetSql0, params.toArray());
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
