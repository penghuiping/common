package com.php25.common.core.dto;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2016/2/16.
 * @description datatable 所需要的数据结构类型
 */
public class DataGridPageDto<T> {

    /**
     * 总记录数
     */
    private Long recordsTotal;

    /**
     * 过滤后总记录数
     */
    private Long recordsFiltered;

    /**
     * 操作次数
     */
    private Integer sEcho;

    private List<T> data;

    private String error;

    /**
     * 透传数据
     */
    private Integer draw;

    public DataGridPageDto() {
        this.data = new ArrayList<>();
        this.recordsTotal = 0L;
        this.recordsFiltered = 0L;
    }

    public DataGridPageDto(Page<T> page) {
        this.data = page.getContent();
        this.recordsTotal = page.getTotalElements();
        this.recordsFiltered = page.getTotalElements();
    }

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
}
