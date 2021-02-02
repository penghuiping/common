package com.php25.common.jdbcsample.postgres.model;

import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.annotation.Table;

/**
 * @author penghuiping
 * @date 2020/1/15 09:58
 */
@Table
public class ShardDepartmentRef {

    @Column("department_id")
    private Long departmentId;

    @Column("customer_id")
    private Long customerId;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
