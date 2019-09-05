package com.php25.common.core.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * orm数据库实体的基类，用于软删除
 *
 * @author penghuiping
 * @date 2016-04-04
 */
@MappedSuperclass
public abstract class BaseSoftDeleteModel extends BaseModel implements Serializable {

    /**
     * 0.无效 1.有效 2.已删除
     */
    @Column
    private Integer enable;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
