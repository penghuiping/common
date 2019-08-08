package com.php25.common.flux.web;

import javax.validation.constraints.Min;

/**
 * @author: penghuiping
 * @date: 2019/7/19 14:04
 * @description:
 */
public class IdLongReq extends BaseDto {

    @Min(value = 0L)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
