package com.php25.common.jdbcsample.oracle.repository;

import com.php25.common.db.Db;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.jdbcsample.oracle.model.Department;
import org.springframework.stereotype.Repository;

/**
 * @author penghuiping
 * @date 2020/1/15 10:07
 */
@Repository
public class DepartmentRepositoryImpl extends BaseDbRepositoryImpl<Department, Long> implements DepartmentRepository {

    public DepartmentRepositoryImpl(Db db) {
        super(db);
    }
}
