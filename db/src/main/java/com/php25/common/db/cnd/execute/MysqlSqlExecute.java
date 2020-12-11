package com.php25.common.db.cnd.execute;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.cnd.GenerationType;
import com.php25.common.db.cnd.sql.DefaultSqlParams;
import com.php25.common.db.cnd.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public MysqlSqlExecute(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int insert(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        Class<?> clazz = defaultSqlParams.getClazz();
        String targetSql = defaultSqlParams.getSql();
        List<Object> params = defaultSqlParams.getParams();
        GenerationType generationType = defaultSqlParams.getGenerationType();
        Object model = defaultSqlParams.getModel();
        try {
            if (GenerationType.IDENTITY.equals(generationType)) {
                //自增操作
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = this.jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(targetSql, Statement.RETURN_GENERATED_KEYS);
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
                int rows = this.jdbcTemplate.update(targetSql, params.toArray());
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
