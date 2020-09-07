package com.php25.common.ws;

import java.lang.reflect.Method;

/**
 * @author penghuiping
 * @date 2020/9/4 14:00
 */
public class ProxyReplyAckHandler implements ReplyAckHandler {

    private final Object srcObject;

    private final Method srcObjectMethod;

    public ProxyReplyAckHandler(Object object, Method method) {
        this.srcObject = object;
        this.srcObjectMethod = method;
    }

    @Override
    public void handle(GlobalSession session,BaseRetryMsg msg) throws Exception {
        this.srcObjectMethod.invoke(srcObject, session, msg);
    }
}
