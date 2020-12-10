package com.php25.common.db.cnd.execute;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.cnd.GenerationType;
import com.php25.common.db.cnd.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/7 14:05
 */
public class OracleSqlExecute extends BaseSqlExecute {

    public OracleSqlExecute(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int insert(SqlParams sqlParams) {
        Class<?> clazz = sqlParams.getClazz();
        String targetSql = sqlParams.getSql();
        List<Object> params = sqlParams.getParams();
        GenerationType generationType = sqlParams.getGenerationType();
        Object model = sqlParams.getModel();
        try {
            if (GenerationType.SEQUENCE.equals(generationType)) {
                //sequence情况
                //获取id field名
                String idField = JdbcModelManager.getPrimaryKeyFieldName(clazz);

                KeyHolder keyHolder = new GeneratedKeyHolder();
                int rows = this.jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(targetSql, new String[]{idField});
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
                if (!field.getType().isAssignableFrom(Long.class)) {
                    throw new DbException("主键必须是Long类型");
                }
                ReflectUtil.getMethod(clazz, "set" + StringUtil.capitalizeFirstLetter(idField), field.getType()).invoke(model, keyHolder.getKey().longValue());
                return rows;
            } else {
                //非sequence情况
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
