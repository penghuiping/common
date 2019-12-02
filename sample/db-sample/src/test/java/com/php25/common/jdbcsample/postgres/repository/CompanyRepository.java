package com.php25.common.jdbcsample.postgres.repository;

import com.php25.common.jdbcsample.postgres.model.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:02
 * @description:
 */
public interface CompanyRepository extends CrudRepository<Company, Long>, CompanyExRepository {
}
