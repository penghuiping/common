package com.php25.common.jdbcsample.mysql.model;

import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.shard.ShardingKey;
import com.php25.common.db.core.shard.TableShard;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/12/24 17:04
 */
@TableShard(logicName = "ShardCustomer", physicName = {"db.t_customer_0", "db.t_customer_1"})
public class ShardCustomer {

    @ShardingKey
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

    @Column("company_id")
    private Long companyId;

    @Column("customer_id")
    private Set<DepartmentRef> departments;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Set<DepartmentRef> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<DepartmentRef> departments) {
        this.departments = departments;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
