package com.php25.common.jdbcsample.oracle.model;

import com.php25.common.db.cnd.annotation.Column;
import com.php25.common.db.cnd.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

/**
 * @author penghuiping
 * @date 2020/1/15 09:47
 */
@Table("t_department")
public class Department implements Persistable<Long> {

    @Id
    private Long id;

    @Column
    private String name;

    @Transient
    private boolean isNew;


    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
