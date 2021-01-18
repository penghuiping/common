package com.php25.common.jdbcsample.postgres.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.postgres.model.DepartmentRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/31 12:42
 */
@Repository
public class DepartmentRefRepositoryImpl implements DepartmentRefRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DbType dbType;


    @Override
    public List<DepartmentRef> findByCustomerId(Long customerId) {
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).whereEq("customerId", customerId).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public void save(List<DepartmentRef> departmentRefs) {
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).insertBatch(departmentRefs);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insertBatch(sqlParams);
    }


    @Override
    public void deleteByCustomerIds(List<Long> customerIds) {
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).whereIn("customerId", customerIds).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

}
