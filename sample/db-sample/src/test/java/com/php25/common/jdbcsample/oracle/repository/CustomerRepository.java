package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.jdbcsample.oracle.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
public interface CustomerRepository extends CrudRepository<Customer, Long>, CustomerExRepository {

}
