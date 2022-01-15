package com.php25.common.timer.po;

/**
 * @author penghuiping
 * @date 2021/3/19 10:42
 */
public class TimerInnerLogPo {

    /**
     * job id
     */
    private String id;

    /**
     * 执行时间(单位毫秒)
     */
    private Long executionTime;

    /**
     * 0:未执行 1:已执行
     */
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
