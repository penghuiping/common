package com.php25.common.jdbcsample.mysql.repository;


import com.php25.common.jdbcsample.mysql.model.DepartmentRef;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/25 17:11
 */
public interface DepartmentRefRepository {

    List<DepartmentRef> findByCustomerId(Long customerId);

    void save(List<DepartmentRef> departmentRefs);

    void deleteByCustomerIds(List<Long> customerIds);
}
