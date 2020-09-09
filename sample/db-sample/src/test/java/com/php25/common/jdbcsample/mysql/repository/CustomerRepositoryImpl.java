package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.Db;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
@Repository
public class CustomerRepositoryImpl extends BaseDbRepositoryImpl<Customer, Long> implements CustomerRepository {

    public CustomerRepositoryImpl(Db db) {
        super(db);
    }
}
