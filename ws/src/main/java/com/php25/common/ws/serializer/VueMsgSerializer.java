package com.php25.common.ws.serializer;


import com.php25.common.core.util.JsonUtil;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.protocal.Pong;
import lombok.extern.log4j.Log4j2;

/**
 * 用于给前端vue的消息格式序列化
 *
 * @author penghuiping
 * @date 2020/9/3 10:32
 */
@Log4j2
public class VueMsgSerializer implements MsgSerializer {

    @Override
    public String from(BaseMsg baseMsg) {
        BaseMsg baseMsg1 = JsonUtil.fromJson(JsonUtil.toJson(baseMsg), BaseMsg.class);
        baseMsg1.setSessionId(null);
        String msg = JsonUtil.toJson(baseMsg1);
        if (!(baseMsg1 instanceof Pong)) {
            log.info("ws response msg:{}", msg);
        }
        return msg;
    }
}
