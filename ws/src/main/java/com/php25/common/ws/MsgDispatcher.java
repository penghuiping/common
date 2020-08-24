package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 消息分发器
 * @author penghuiping
 * @date 2020/08/10
 */
@Slf4j
public class MsgDispatcher {

    private ConcurrentHashMap<String, MsgHandler<BaseRetryMsg>> handlers =new ConcurrentHashMap<>();

    private GlobalSession session;

    private ExecutorService executorService;

    public void setSession(GlobalSession session) {
        this.session = session;
    }

    public MsgDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void dispatch(BaseRetryMsg baseRetry) {
        executorService.submit(()->{
            try {
                String action = baseRetry.getAction();
                MsgHandler<BaseRetryMsg> handler = handlers.get(action);
                handler.handle(session,baseRetry);
            }catch (Exception e) {
                log.error("发送websocket消息出错",e);
                throw new IllegalStateException("发送websocket消息出错",e);
            }
        });
    }

    public void registerHandler(String name, MsgHandler<BaseRetryMsg> msgHandler) {
        handlers.put(name,msgHandler);
    }






}
