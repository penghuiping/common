package com.php25.common.ws.serializer;


import com.php25.common.ws.protocal.BaseMsg;

/**
 * @author penghuiping
 * @date 2020/9/3 10:29
 */
public interface MsgSerializable {

    /**
     * 把对象转化为字符串
     *
     * @param baseMsg
     * @return
     */
    String from(BaseMsg baseMsg);
}
