package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.jdbcsample.oracle.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author penghuiping
 * @date 2018/8/31 15:03
 */
@Repository
public class CompanyRepositoryImpl extends BaseDbRepositoryImpl<Company, Long> implements CompanyRepository {

    public CompanyRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        super(jdbcTemplate, dbType);
    }
}
