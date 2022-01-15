package com.php25.common.jdbcsample.mysql.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Transient
    private boolean isNew;

    public Department() {
    }

    public Department(Long id, String name, boolean isNew) {
        this.id = id;
        this.name = name;
        this.isNew = isNew;
    }

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
