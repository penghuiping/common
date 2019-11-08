package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.Db;
import com.php25.common.db.repository.JdbcDbRepositoryImpl;
import com.php25.common.jdbcsample.oracle.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
@Repository
public class CustomerRepositoryImpl extends JdbcDbRepositoryImpl<Customer, Long> implements CustomerExRepository {


    @Autowired
    private Db db;

    @Override
    public Customer save0(Customer entity) {
        db.cndJdbc(Customer.class).insert(entity);
        return entity;
    }

    @Override
    public void saveAll0(List<Customer> customers) {
        db.cndJdbc(Customer.class).insertBatch(customers);
    }
}
