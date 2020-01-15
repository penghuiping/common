package com.php25.common.db;

import com.php25.common.db.cnd.CndJdbc;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {
    private JdbcOperations jdbcOperations;

    private DbType dbType;

    public DbType getDbType() {
        return dbType;
    }

    public Db(DbType dbType) {
        this.dbType = dbType;
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    /**
     * 获取一个关系型数据库 新条件
     *
     * @return
     */
    public CndJdbc cndJdbc(Class cls) {
        return CndJdbc.of(cls, dbType, this.jdbcOperations);
    }


}