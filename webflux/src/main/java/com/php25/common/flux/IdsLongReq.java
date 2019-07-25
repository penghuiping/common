package com.php25.common.flux;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: penghuiping
 * @date: 2019/7/19 14:05
 * @description:
 */
public class IdsLongReq {

    @NotNull
    @Size(min = 1)
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
