package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.repository.JdbcDbRepository;
import com.php25.common.jdbcsample.oracle.model.Company;

/**
 * @author: penghuiping
 * @date: 2019/8/24 15:38
 * @description:
 */
public interface CompanyExRepository extends JdbcDbRepository<Company, Long> {

    Company save0(Company entity);
}
