package com.php25.common.db.core.execute;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.JdbcModelRowMapper;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.BatchSqlParams;
import com.php25.common.db.core.sql.DefaultSqlParams;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.exception.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/12/4 22:33
 */
public abstract class BaseSqlExecute implements SqlExecute {
    private static final Logger log = LoggerFactory.getLogger(BaseSqlExecute.class);

    protected final JdbcTemplate jdbcTemplate;

    public BaseSqlExecute(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> select(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        String targetSql = defaultSqlParams.getSql();
        Object[] paras = defaultSqlParams.getParams().toArray();
        Class<?> resultType = defaultSqlParams.getResultType();
        List<T> list = null;
        if (resultType.isAssignableFrom(Map.class)) {
            list = (List<T>) this.jdbcTemplate.query(targetSql, paras, new ColumnMapRowMapper());
        } else {
            list = this.jdbcTemplate.query(targetSql, paras, new JdbcModelRowMapper<T>((Class<T>) resultType));
        }
        return list;
    }

    /**
     * insert时候进行参数转化
     * <p>
     * 对于自定义类型class，需要获取这个class的primary key值
     *
     * @param paramValue 源参数值
     * @return 最终参数值
     */
    private Object paramConvert(Object paramValue) {
        if (null == paramValue) {
            return null;
        }
        Class<?> paramValueType = paramValue.getClass();
        if (paramValueType.isPrimitive() || Boolean.class.isAssignableFrom(paramValueType) || Number.class.isAssignableFrom(paramValueType) || String.class.isAssignableFrom(paramValueType)) {
            //基本类型,string,date直接加入参数列表
            return paramValue;
        } else if (Date.class.isAssignableFrom(paramValueType)) {
            Date tmp = (Date) paramValue;
            return new Timestamp(tmp.getTime());
        } else if (LocalDateTime.class.isAssignableFrom(paramValueType)) {
            LocalDateTime tmp = (LocalDateTime) paramValue;
            return new Timestamp(Date.from(tmp.toInstant(ZoneOffset.ofHours(8))).getTime());
        } else {
            if (!(Collection.class.isAssignableFrom(paramValueType))) {
                //自定义class类，通过反射获取主键值，在加入参数列表
                String subClassPk = JdbcModelManager.getPrimaryKeyFieldName(paramValueType);
                try {
                    return ReflectUtil.getMethod(paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)).invoke(paramValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DbException(String.format("%s没有%s方法", paramValueType, "get" + StringUtil.capitalizeFirstLetter(subClassPk)), e);
                }
            } else {
                //Collection类型不做任何处理
                throw new DbException("此orm框架中model中不支持Collection类型的属性");
            }
        }
    }

    @Override
    public List<Map> mapSelect(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        defaultSqlParams.setModel(Map.class);
        return this.select(sqlParams);
    }


    @Override
    public Map mapSingle(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
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
            DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
            return this.jdbcTemplate.update(defaultSqlParams.getSql(), defaultSqlParams.getParams().toArray());
        } catch (Exception e) {
            throw new DbException("更新操作失败", e);
        }
    }

    @Override
    public int[] updateBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            return this.jdbcTemplate.batchUpdate(batchSqlParams.getSql(), batchSqlParams.getBatchParams());
        } catch (Exception e) {
            throw new DbException("批量更新操作失败", e);
        }
    }

    @Override
    public int[] insertBatch(SqlParams sqlParams) {
        try {
            BatchSqlParams batchSqlParams = (BatchSqlParams) sqlParams;
            return this.jdbcTemplate.batchUpdate(batchSqlParams.getSql(), batchSqlParams.getBatchParams());
        } catch (Exception e) {
            throw new DbException("插入操作失败", e);
        }
    }

    @Override
    public int delete(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        return this.jdbcTemplate.update(defaultSqlParams.getSql(), defaultSqlParams.getParams().toArray());
    }

    @Override
    public long count(SqlParams sqlParams) {
        DefaultSqlParams defaultSqlParams = (DefaultSqlParams) sqlParams;
        Long result = this.jdbcTemplate.queryForObject(defaultSqlParams.getSql(), defaultSqlParams.getParams().toArray(), Long.class);
        if (null == result) {
            result = -1L;
        }
        return result;
    }
}
