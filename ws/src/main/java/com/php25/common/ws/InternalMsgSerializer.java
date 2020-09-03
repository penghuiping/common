package com.php25.common.ws;


import com.php25.common.core.util.JsonUtil;

/**
 * @author penghuiping
 * @date 2020/9/3 10:32
 */
public class InternalMsgSerializer implements MsgSerializable {

    @Override
    public String from(BaseRetryMsg baseRetryMsg) {
        return JsonUtil.toJson(baseRetryMsg);
    }
}
