package com.php25.common.ws.handler;

import com.php25.common.ws.SessionContext;
import com.php25.common.ws.protocal.BaseMsg;

import java.lang.reflect.Method;

/**
 * @author penghuiping
 * @date 2020/9/4 13:37
 */
public class ProxyMsgHandler implements MsgHandler<BaseMsg> {

    private final Object srcObject;

    private final Class<?> cls;

    private final Method srcObjectMethod;

    public ProxyMsgHandler(Object object, Method method, Class<?> cls) {
        this.srcObject = object;
        this.srcObjectMethod = method;
        this.cls = cls;
    }

    @Override
    public void handle(SessionContext session, BaseMsg msg) throws Exception {
        this.srcObjectMethod.invoke(cls.cast(this.srcObject), session, msg);
    }
}
