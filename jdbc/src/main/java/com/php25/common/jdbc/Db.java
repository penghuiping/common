package com.php25.common.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {

    private Logger log = LoggerFactory.getLogger(Db.class);

    private JdbcOperations jdbcOperations;

    private DbType dbType;

    public Db(JdbcOperations jdbcOperations, DbType dbType) {
        this.jdbcOperations = jdbcOperations;
        this.dbType = dbType;
    }

    /**
     * 获取一个新条件
     *
     * @return
     */
    public Cnd cnd(Class cls) {
        return Cnd.of(cls, dbType, this.jdbcOperations);
    }


}