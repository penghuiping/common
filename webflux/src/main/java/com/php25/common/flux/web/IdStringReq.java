package com.php25.common.flux.web;

import javax.validation.constraints.NotBlank;

/**
 * @author: penghuiping
 * @date: 2019/7/19 14:01
 * @description:
 */
public class IdStringReq extends BaseDto {

    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
