package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * sessionId 与 userId的关系
 *
 * @author penghuiping
 * @date 20/8/11 17:36
 */
@Setter
@Getter
public class SidUid {

    /**
     * websocket的会话id
     */
    private String sessionId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 服务器id
     */
    private String serverId;
}
