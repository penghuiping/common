package com.php25.common.ws;


/**
 * @author penghuiping
 * @date 2020/9/3 10:29
 */
public interface MsgSerializable {

    /**
     * 把对象转化为字符串
     *
     * @param baseRetryMsg
     * @return
     */
    String from(BaseRetryMsg baseRetryMsg);
}
