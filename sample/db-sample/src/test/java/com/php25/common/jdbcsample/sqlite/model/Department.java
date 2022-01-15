package com.php25.common.jdbcsample.sqlite.model;

import com.php25.common.db.mapper.annotation.Column;
import com.php25.common.db.mapper.annotation.Table;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
