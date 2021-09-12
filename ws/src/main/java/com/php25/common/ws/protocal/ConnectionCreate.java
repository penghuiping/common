package com.php25.common.ws.protocal;

import com.php25.common.ws.annotation.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:59
 */
@Setter
@Getter
@WsMsg(action = "connection_create")
public class ConnectionCreate extends BaseMsg {
}
