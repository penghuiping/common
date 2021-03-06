package com.php25.common.core.util.jwt;

import com.php25.common.core.util.StringUtil;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/3/6 09:27
 */
public class UserRoleInfo {

    /**
     * 令牌唯一id
     */
    private String jti;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色列表
     */
    private List<String> roleNames;

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public boolean isValid() {
        if (StringUtil.isBlank(username)) {
            return false;
        }

        if (StringUtil.isBlank(jti)) {
            return false;
        }

        return null != roleNames && !roleNames.isEmpty();
    }
}
