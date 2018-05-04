package com.php25.common.model;

import org.nutz.dao.entity.annotation.Name;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Entity
@Table(name = "t_customer")
@org.nutz.dao.entity.annotation.Table("t_customer")
public class Customer {

    @Id
    @org.nutz.dao.entity.annotation.Id(auto = false)
    //@Name
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    @org.nutz.dao.entity.annotation.Column
    private String username;

    @Column
    @org.nutz.dao.entity.annotation.Column
    private String password;

    @Column(name = "create_time")
    @org.nutz.dao.entity.annotation.Column("create_time")
    private Date createTime;

    @Column(name = "update_time")
    @org.nutz.dao.entity.annotation.Column("update_time")
    private Date updateTime;

    @Column
    @org.nutz.dao.entity.annotation.Column
    private Integer enable;

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

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
