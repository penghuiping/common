package com.php25.common.core.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 *
 * orm 数据库实体的基类
 *
 * @author penghuiping
 * @date 2016-04-03
 *
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
