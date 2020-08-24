package com.php25.common.ws;

import lombok.Getter;
import lombok.Setter;

/**
 * sessionId 与 userId的关系
 * @author penghuiping
 * @date 20/8/11 17:36
 */
@Setter
@Getter
class SidUid {

    private String sessionId;

    private String userId;

    private String serverId;
}
