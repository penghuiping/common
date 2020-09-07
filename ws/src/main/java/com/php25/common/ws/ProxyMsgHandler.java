package com.php25.common.ws;

import java.lang.reflect.Method;

/**
 * @author penghuiping
 * @date 2020/9/4 13:37
 */
public class ProxyMsgHandler implements MsgHandler<BaseRetryMsg> {

    private final Object srcObject;

    private final Class<?> cls;

    private final Method srcObjectMethod;

    public ProxyMsgHandler(Object object, Method method, Class<?> cls) {
        this.srcObject = object;
        this.srcObjectMethod = method;
        this.cls = cls;
    }

    @Override
    public void handle(GlobalSession session,BaseRetryMsg msg) throws Exception {
        this.srcObjectMethod.invoke(cls.cast(this.srcObject), session, msg);
    }
}
