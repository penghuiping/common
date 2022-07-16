package com.php25.common.ws.serializer;


import com.php25.common.ws.protocal.BaseMsg;

/**
 * @author penghuiping
 * @date 2020/9/3 10:29
 */
public interface MsgSerializer {

    /**
     * 把消息对象转化为字符串
     *
     * @param baseMsg 消息对象
     * @return 序列化后的字符串
     */
    String from(BaseMsg baseMsg);
}
