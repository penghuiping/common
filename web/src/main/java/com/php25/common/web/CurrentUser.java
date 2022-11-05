package com.php25.common.web;

import java.util.List;

/**
 * 当前登录用户
 *
 * @author penghuiping
 * @date 2022/11/5 18:18
 */
public abstract class CurrentUser {

    /**
     * 用户code用于唯一标识用户
     */
    private String userCode;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private List<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
