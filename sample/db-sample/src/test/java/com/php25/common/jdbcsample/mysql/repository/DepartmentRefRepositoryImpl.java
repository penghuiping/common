package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.jdbcsample.mysql.model.DepartmentRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/25 17:13
 */
@Repository
public class DepartmentRefRepositoryImpl implements DepartmentRefRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DbType dbType;

    @Override
    public List<DepartmentRef> findByCustomerId(Long customerId) {
        return QueriesExecute.of(dbType)
                .singleJdbc().with(jdbcTemplate)
                .select(Queries.of(dbType).from(DepartmentRef.class)
                        .whereEq("customerId", customerId).select());
    }

    @Override
    public void save(List<DepartmentRef> departmentRefs) {
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .insertBatch(Queries.of(dbType)
                        .from(DepartmentRef.class)
                        .insertBatch(departmentRefs));
    }


    @Override
    public void deleteByCustomerIds(List<Long> customerIds) {
        QueriesExecute.of(dbType).singleJdbc()
                .with(jdbcTemplate)
                .delete(Queries.of(dbType)
                        .from(DepartmentRef.class)
                        .whereIn("customerId", customerIds)
                        .delete());
    }
}
