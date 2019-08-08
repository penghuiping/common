package com.php25.common.flux.web;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: penghuiping
 * @date: 2019/7/19 14:03
 * @description:
 */
public class IdsStringReq extends BaseDto {

    @NotNull
    @Size(min = 1)
    List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
