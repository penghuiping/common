package com.php25.common.flux;

/**
 * @author: penghuiping
 * @date: 2019/7/28 13:24
 * @description:
 */
public abstract class BaseDto {

    String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
