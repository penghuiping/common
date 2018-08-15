package com.php25.common.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author GavinKing
 */
public class Db {

    private Logger log = LoggerFactory.getLogger(Db.class);

    private JdbcOperations jdbcOperations;

    public Db(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * 获取一个新条件
     *
     * @return
     */
    public Cnd cnd(Class cls) {
        return Cnd.of(cls, this.jdbcOperations);
    }


}