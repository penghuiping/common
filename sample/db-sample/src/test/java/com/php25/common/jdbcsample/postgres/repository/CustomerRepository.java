package com.php25.common.jdbcsample.postgres.repository;

import com.php25.common.jdbcsample.postgres.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 21:53
 * @Description:
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, CustomerExRepository {

}
