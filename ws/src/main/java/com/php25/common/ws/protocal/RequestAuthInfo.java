package com.php25.common.ws.protocal;

import com.php25.common.ws.annotation.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:10
 */
@Setter
@Getter
@WsMsg(action = "request_auth_info")
public class RequestAuthInfo extends BaseMsg {
}
