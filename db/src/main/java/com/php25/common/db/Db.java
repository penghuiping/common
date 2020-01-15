package com.php25.common.db;

import com.php25.common.db.cnd.CndJdbc;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionOperations;

/**
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {
    private JdbcOperations jdbcOperations;

    private TransactionOperations transactionOperations;

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

    public void setTransactionOperations(TransactionOperations transactionOperations) {
        this.transactionOperations = transactionOperations;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    public TransactionOperations getTransactionOperations() {
        return transactionOperations;
    }

    /**
     * 获取一个新条件
     *
     * @return
     */
    public CndJdbc cndJdbc(Class cls) {
        return CndJdbc.of(cls, dbType, this.jdbcOperations);
    }


}