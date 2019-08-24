package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.repository.JdbcDbRepository;
import com.php25.common.jdbcsample.oracle.model.Customer;

/**
 * @author: penghuiping
 * @date: 2019/8/24 15:38
 * @description:
 */
public interface CustomerExRepository extends JdbcDbRepository<Customer, Long> {
}
