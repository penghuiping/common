package com.php25.common.timer.entity;


import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.annotation.Table;

/**
 * @author penghuiping
 * @date 2021/3/19 10:42
 */
@Table("t_timer_inner_log")
public class TimerInnerLog {

    private String id;

    /**
     * 执行时间(单位毫秒)
     */
    @Column("execution_time")
    private Long executionTime;

    /**
     * 0:未执行 1:已执行
     */
    @Column("status")
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
