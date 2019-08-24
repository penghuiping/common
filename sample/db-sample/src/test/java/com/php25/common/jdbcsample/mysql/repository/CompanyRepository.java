package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.jdbcsample.mysql.model.Company;
import org.springframework.data.repository.CrudRepository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:02
 * @description:
 */
public interface CompanyRepository extends CrudRepository<Company, Long>, CompanyExRepository {
}
