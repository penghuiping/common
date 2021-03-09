package com.php25.common.ws;


import com.php25.common.core.util.JsonUtil;
import lombok.extern.log4j.Log4j2;

/**
 * @author penghuiping
 * @date 2020/9/3 10:32
 */
@Log4j2
public class VueMsgSerializer implements MsgSerializable {

    @Override
    public String from(BaseRetryMsg baseRetryMsg) {
        BaseRetryMsg baseRetryMsg1 = JsonUtil.fromJson(JsonUtil.toJson(baseRetryMsg), BaseRetryMsg.class);
        baseRetryMsg1.setSessionId(null);
        baseRetryMsg1.setInterval(null);
        baseRetryMsg1.setMaxRetry(null);
        baseRetryMsg1.setCount(null);
        String msg = JsonUtil.toJson(baseRetryMsg1);
        if (!(baseRetryMsg instanceof Pong)) {
            log.info("ws response msg:{}", msg);
        }
        return msg;
    }
}
