package com.php25.common.ws;

/**
 * @author penghuiping
 * @date 20/8/12 16:05
 */
public interface SecurityAuthentication {

    /**
     * 验证token
     * @param token 令牌
     * @return 返回 uid
     */
    String authenticate(String token);
}
