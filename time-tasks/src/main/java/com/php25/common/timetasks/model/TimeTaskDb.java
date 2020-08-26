package com.php25.common.timetasks.model;

import com.php25.common.db.cnd.annotation.Column;
import com.php25.common.db.cnd.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/8/24 13:52
 */
@Table("t_time_task")
public class TimeTaskDb implements Persistable<String> {

    @Id
    private String id;

    @Column("class_name")
    private String className;

    @Column("execute_time")
    private LocalDateTime executeTime;

    @Column
    private String cron;

    @Column
    private Integer enable;

    @Transient
    private Boolean isNew;

    @Override
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

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
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

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
