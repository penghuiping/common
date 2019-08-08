package com.php25.common.coresample.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Date;

/**
 * Created by penghuiping on 2018/5/1.
 */
@JacksonXmlRootElement(namespace = "customer",localName ="customer")
public class CustomerDto {

    @JacksonXmlCData(value =true)
    @Excel(name = "编号", height = 5, width = 10, isImportField = "true_st")
    private Long id;

    @JacksonXmlCData(value =true)
    @Excel(name = "姓名", height = 5, width = 20, isImportField = "true_st")
    private String username;

    @JacksonXmlCData(value =true)
    @Excel(name = "密码", height = 5, width = 20, isImportField = "true_st")
    private String password;

    @JacksonXmlCData(value =true)
    @JacksonXmlProperty(localName ="create_time")
    @Excel(name = "创建日期", databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", isImportField = "true_st", width = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    @JacksonXmlCData(value =true)
    @JacksonXmlProperty(localName ="update_time")
    @Excel(name = "更新日期", databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", isImportField = "true_st", width = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date updateTime;

    @JacksonXmlCData(value =true)
    @Excel(name = "是否有效", height = 5, width = 10, isImportField = "true_st")
    private Integer enable;

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
