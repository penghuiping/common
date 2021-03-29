package com.php25.common.mq.rabbit;

import com.php25.common.mq.MessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2021/3/29 13:39
 */
class MessageHandlerContainer {

    private final List<MessageHandler> handlers = new ArrayList<>();
    private int count = 0;

    /**
     * 添加消息处理器
     *
     * @param handler 消息处理器
     */
    synchronized void add(MessageHandler handler) {
        handlers.add(handler);
    }

    /**
     * 轮训的方式获取handler
     *
     * @return 消息处理器
     */
    synchronized MessageHandler getMessageHandlerRoundRobin() {
        int size = handlers.size();
        int index = count % size;
        MessageHandler handler = handlers.get(index);
        if (count == Integer.MAX_VALUE) {
            count = 0;
        } else {
            count++;
        }
        return handler;
    }
}
