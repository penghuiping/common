package com.php25.common.db.cnd;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author penghuiping
 * @date 2020/9/14 16:03
 */
public class JdbcPair {

    private JdbcOperations jdbcOperations;

    private TransactionTemplate transactionTemplate;

    public JdbcPair(JdbcOperations jdbcOperations, TransactionTemplate transactionTemplate) {
        this.jdbcOperations = jdbcOperations;
        this.transactionTemplate = transactionTemplate;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
