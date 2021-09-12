package com.php25.common.ws.handler;

import com.php25.common.ws.SessionContext;
import com.php25.common.ws.protocal.BaseMsg;

/**
 * 消息处理接口
 *
 * @author penghuiping
 * @date 2020/08/10
 */
public interface MsgHandler<T extends BaseMsg> {

    void handle(SessionContext session, T msg) throws Exception;

}
