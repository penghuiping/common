package com.php25.common.db.sql;

import com.php25.common.db.mapper.GenerationType;
import com.php25.common.db.mapper.annotation.Column;
import com.php25.common.db.mapper.annotation.GeneratedValue;
import com.php25.common.db.mapper.annotation.Id;
import com.php25.common.db.mapper.annotation.Table;
import com.php25.common.db.mapper.annotation.Transient;
import com.php25.common.db.mapper.annotation.Version;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2018/8/30 09:33
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

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
