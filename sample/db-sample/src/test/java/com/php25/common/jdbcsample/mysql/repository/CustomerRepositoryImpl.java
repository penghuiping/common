package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.repository.JdbcDbRepositoryImpl;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
@Repository
public class CustomerRepositoryImpl extends JdbcDbRepositoryImpl<Customer, Long> implements CustomerExRepository {

}
