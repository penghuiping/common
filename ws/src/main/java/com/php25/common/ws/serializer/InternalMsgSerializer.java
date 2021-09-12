package com.php25.common.ws.serializer;


import com.php25.common.core.util.JsonUtil;
import com.php25.common.ws.protocal.BaseMsg;

/**
 * @author penghuiping
 * @date 2020/9/3 10:32
 */
public class InternalMsgSerializer implements MsgSerializable {

    @Override
    public String from(BaseMsg baseRetryMsg) {
        return JsonUtil.toJson(baseRetryMsg);
    }
}
