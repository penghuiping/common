package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.Db;
import com.php25.common.db.repository.JdbcDbRepositoryImpl;
import com.php25.common.jdbcsample.oracle.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author: penghuiping
 * @date: 2018/8/31 15:03
 * @description:
 */
@Repository
public class CompanyRepositoryImpl extends JdbcDbRepositoryImpl<Company, Long> implements CompanyExRepository {

    @Autowired
    private Db db;

    @Override
    public Company save0(Company entity) {
        db.cndJdbc(Company.class).insert(entity);
        return entity;
    }
}
