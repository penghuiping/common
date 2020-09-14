package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.repository.BaseDbRepository;
import com.php25.common.jdbcsample.mysql.model.Department;

/**
 * @author penghuiping
 * @date 2020/9/14 14:48
 */
public interface ShardDepartmentRepository extends BaseDbRepository<Department, Long> {
}
