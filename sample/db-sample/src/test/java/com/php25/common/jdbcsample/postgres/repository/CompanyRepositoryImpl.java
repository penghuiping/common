package com.php25.common.jdbcsample.postgres.repository;

import com.php25.common.db.repository.JdbcDbRepositoryImpl;
import com.php25.common.jdbcsample.postgres.model.Company;
import org.springframework.stereotype.Repository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:03
 * @description:
 */
@Repository
public class CompanyRepositoryImpl extends JdbcDbRepositoryImpl<Company, Long> implements CompanyExRepository {
}