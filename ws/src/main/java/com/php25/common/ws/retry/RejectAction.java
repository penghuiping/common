package com.php25.common.ws.retry;

/**
 * @author penghuiping
 * @date 2021/8/22 22:01
 */
public interface RejectAction<T> {

    /**
     * 丢弃操作
     *
     * @param value 对象
     */
    void doAction(T value);
}
