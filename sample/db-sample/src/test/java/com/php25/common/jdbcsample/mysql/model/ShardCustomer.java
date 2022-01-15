package com.php25.common.jdbcsample.mysql.model;

import com.php25.common.db.mapper.annotation.Column;
import com.php25.common.db.mapper.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/12/24 17:04
 */
@Table
public class ShardCustomer {

    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private Integer age;

    @Column
    private String password;

    @Column("create_time")
    private LocalDateTime startTime;

    @Column("update_time")
    private LocalDateTime updateTime;

    @Column
    private Integer enable;

    @Column
    private BigDecimal score;

    @Version
    @Column
    private Long version;

    @Transient
    private Boolean isNew;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
