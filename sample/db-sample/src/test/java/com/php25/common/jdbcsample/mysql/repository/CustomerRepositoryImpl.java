package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author penghuiping
 * @date 2018/8/16 21:53
 */
@Repository
public class CustomerRepositoryImpl extends BaseDbRepositoryImpl<Customer, Long> implements CustomerRepository {

    public CustomerRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        super(jdbcTemplate, dbType);
    }
}
