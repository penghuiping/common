package com.php25.common.ws.protocal;

import com.php25.common.ws.handler.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:51
 */
@Setter
@Getter
@WsMsg(action = "connection_close")
public class ConnectionClose extends BaseMsg {
}
