package com.php25.common.jdbcsample.mysql.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.php25.common.db.core.annotation.Column;
import com.php25.common.db.core.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

/**
 * @author penghuiping
 * @date 2021/1/5 20:21
 */
@Table("t_department")
public class ShardDepartment implements Persistable<Long> {
    @Id
    private Long id;

    @Column
    private String name;

    @JsonIgnore
    @Transient
    private boolean isNew;


    @Override
    public Long getId() {
        return id;
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

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
