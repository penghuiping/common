package com.php25.common.coresample.dto;

import com.php25.common.core.tree.TreeAble;

/**
 * @author penghuiping
 * @date 2020/7/9 10:12
 */
public class Department implements TreeAble<String> {

    private String id;

    private String parentId;

    private String name;

    public Department(String id, String parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
