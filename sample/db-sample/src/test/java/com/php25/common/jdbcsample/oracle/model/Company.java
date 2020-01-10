package com.php25.common.jdbcsample.oracle.model;

import com.php25.common.db.cnd.Column;
import com.php25.common.db.cnd.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Date;
import java.util.List;

/**
 * @author: penghuiping
 * @date: 2018/8/30 09:33
 * @description:
 */
@Table("t_company")
public class Company implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    @Transient
    private List<Customer> customers;

    private Integer enable;

    @Column(value = "create_time")
    private Date createTime;

    @Column(value = "update_time")
    private Date updateTime;

    @Transient
    private Boolean isNew;

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

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

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
