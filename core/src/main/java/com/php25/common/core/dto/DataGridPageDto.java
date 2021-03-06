package com.php25.common.core.dto;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * DataGrid 所需要的数据结构类型
 *
 * @author penghuiping
 * @date 2016/2/16.
 */
public class DataGridPageDto<T> {

    /**
     * 总记录数
     */
    private Long recordsTotal;

    private List<T> data;

    public DataGridPageDto() {
        this.data = new ArrayList<>();
        this.recordsTotal = 0L;
    }

    public DataGridPageDto(Page<T> page) {
        this.data = page.getContent();
        this.recordsTotal = page.getTotalElements();
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
