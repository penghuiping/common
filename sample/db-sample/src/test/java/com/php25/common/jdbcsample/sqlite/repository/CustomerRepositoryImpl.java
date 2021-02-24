package com.php25.common.jdbcsample.sqlite.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.jdbcsample.sqlite.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
@Repository
public class CustomerRepositoryImpl extends BaseDbRepositoryImpl<Customer, Long> implements CustomerRepository {

    public CustomerRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        super(jdbcTemplate, dbType);
    }
}
