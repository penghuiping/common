package com.php25.common.db.sql;

import com.php25.common.db.mapper.annotation.Column;
import com.php25.common.db.mapper.annotation.Id;
import com.php25.common.db.mapper.annotation.Table;
import com.php25.common.db.mapper.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Date;
import java.util.List;

/**
 * @author penghuiping
 * @date 2018/8/30 09:33
 */
@Table("t_company")
public class Company implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    @Transient
    private List<Customer> customers;

    private Integer enable;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Transient
    private Boolean isNew;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
