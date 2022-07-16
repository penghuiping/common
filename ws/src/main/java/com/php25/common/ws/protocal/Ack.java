package com.php25.common.ws.protocal;

import com.php25.common.ws.handler.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2020/8/13 17:44
 */
@Setter
@Getter
@WsMsg(action = "ack")
public class Ack extends BaseMsg {

}
