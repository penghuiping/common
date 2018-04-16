package com.php25.common.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by penghuiping on 16/4/4.
 */
@MappedSuperclass
public abstract class BaseSoftDeleteModel extends BaseModel implements Serializable {

    @Column
    private Integer enable;//0.无效 1.有效 2.已删除

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
