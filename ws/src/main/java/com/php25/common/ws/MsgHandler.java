package com.php25.common.ws;

/**
 * 消息处理接口
 *
 * @author penghuiping
 * @date 2020/08/10
 */
public interface MsgHandler<T extends BaseRetryMsg> {

    void handle(GlobalSession session, T msg) throws Exception;

}
