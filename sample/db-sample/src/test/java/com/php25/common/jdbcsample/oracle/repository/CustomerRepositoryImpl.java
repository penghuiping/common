package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.repository.BaseJpaRepositoryImpl;
import com.php25.common.jdbcsample.oracle.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
@Repository
public class CustomerRepositoryImpl extends BaseJpaRepositoryImpl<Customer, Long> implements CustomerRepository {

}
