package com.php25.common.jdbcsample.sqlite.repository;


import com.php25.common.jdbcsample.sqlite.model.DepartmentRef;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/31 12:42
 */
public interface DepartmentRefRepository {

    List<DepartmentRef> findByCustomerId(Long customerId);

    void save(List<DepartmentRef> departmentRefs);

    void deleteByCustomerIds(List<Long> customerIds);
}
