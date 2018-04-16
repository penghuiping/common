package com.php25.common.dto;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penghuiping on 2016/2/16.
 * datatable 所需要的数据结构类型
 */
public class DataGridPageDto<T> implements Serializable{
    private Long recordsTotal;//总记录数

    private Long recordsFiltered;//过滤后总记录数

    private Integer sEcho;  //操作次数

    private List<T> data;

    private String error;

    private Integer draw;//透传数据

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Integer getsEcho() {
        return sEcho;
    }

    public void setsEcho(Integer sEcho) {
        this.sEcho = sEcho;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }


    public DataGridPageDto(){

        this.data = new ArrayList<>();
        this.setRecordsTotal(0L);
        this.setRecordsFiltered(0L);
    }
    public DataGridPageDto(Page<T> page) {
        this.data = page.getContent();
        this.recordsTotal = page.getTotalElements();
        this.recordsFiltered = page.getTotalElements();
    }
}
