package com.php25.common.ws.protocal;

import com.php25.common.ws.annotation.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2020/8/17 16:49
 */
@Setter
@Getter
@WsMsg(action = "pong")
public class Pong extends BaseMsg {
}
