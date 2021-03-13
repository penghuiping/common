package com.php25.common.timer.model;

import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.annotation.Table;
import org.springframework.data.annotation.Id;

/**
 * @author penghuiping
 * @date 2020/8/24 13:52
 */
@Table("t_timer_job")
public class JobModel {

    /**
     * 任务id
     */
    @Id
    private String id;

    /**
     * 任务对应的java执行代码
     */
    @Column("class_name")
    private String className;

    /**
     * 任务的cron表达式
     */
    @Column
    private String cron;

    /**
     * 0:无效 1:有效
     */
    @Column
    private Integer enable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
