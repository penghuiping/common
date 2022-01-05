package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.oracle.model.DepartmentRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.php25.common.db.core.sql.column.Columns.col;

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
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).whereEq(col(DepartmentRef::getCustomerId), customerId).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public void save(List<DepartmentRef> departmentRefs) {
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).insertBatch(departmentRefs);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insertBatch(sqlParams);
    }


    @Override
    public void deleteByCustomerIds(List<Long> customerIds) {
        SqlParams sqlParams = Queries.of(dbType).from(DepartmentRef.class).whereIn(col(DepartmentRef::getCustomerId), customerIds).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

}
