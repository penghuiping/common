package com.php25.common.jpasample.oracle.repository;

import com.php25.common.db.repository.BaseJpaRepositoryImpl;
import com.php25.common.jpasample.oracle.model.Company;
import org.springframework.stereotype.Repository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:03
 * @description:
 */
@Repository
public class CompanyRepositoryImpl extends BaseJpaRepositoryImpl<Company, Long> implements CompanyRepository {
}
