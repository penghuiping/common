package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.jdbcsample.mysql.model.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
public interface CustomerRepository extends CrudRepository<Customer, Long>, CustomerExRepository {

}
