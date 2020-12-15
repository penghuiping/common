package com.php25.common.jdbcsample.mysql.model;

import com.php25.common.db.core.GenerationType;
import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.annotation.GeneratedValue;
import com.php25.common.db.core.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Table("t_customer")
public class Customer implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Set<DepartmentRef> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<DepartmentRef> departments) {
        this.departments = departments;
    }
}
