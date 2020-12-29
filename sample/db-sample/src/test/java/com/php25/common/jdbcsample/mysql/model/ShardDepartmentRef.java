package com.php25.common.jdbcsample.mysql.model;

import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.shard.ShardingKey;
import com.php25.common.db.core.shard.TableShard;

/**
 * @author penghuiping
 * @date 2020/1/15 09:58
 */
@TableShard(logicName = "ShardDepartmentRef", physicName = {"db.t_customer_department_0", "db.t_customer_department_1"})
public class ShardDepartmentRef {

    @Column("department_id")
    private Long departmentId;

    @ShardingKey
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
