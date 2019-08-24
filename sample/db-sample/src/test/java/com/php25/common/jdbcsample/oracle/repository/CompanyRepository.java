package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.jdbcsample.oracle.model.Company;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:02
 * @description:
 */
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long>, CompanyExRepository {
}
