package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.repository.JdbcDbRepository;
import com.php25.common.jdbcsample.oracle.model.Company;
import org.springframework.data.repository.CrudRepository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:02
 * @description:
 */
public interface CompanyRepository extends CrudRepository<Company,Long>, JdbcDbRepository<Company,Long> {
}
