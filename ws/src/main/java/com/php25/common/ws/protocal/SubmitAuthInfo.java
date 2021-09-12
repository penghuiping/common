package com.php25.common.ws.protocal;

import com.php25.common.ws.annotation.WsMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 20/8/12 16:15
 */
@Setter
@Getter
@WsMsg(action = "submit_auth_info")
public class SubmitAuthInfo extends BaseMsg {
    private String token;
}
