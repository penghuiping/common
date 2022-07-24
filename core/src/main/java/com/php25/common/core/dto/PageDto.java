package com.php25.common.core.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页所需要的数据结构类型
 *
 * @author penghuiping
 * @date 2016/2/16.
 */
public class PageDto<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 分页数据
     */
    private List<T> data;

    public PageDto() {
        this.data = new ArrayList<>();
        this.total = 0L;
    }

    public static <T> PageDto<T> empty() {
        return new PageDto<>();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
