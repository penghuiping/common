package com.php25.common.ws.retry;

/**
 * @author penghuiping
 * @date 2021/8/22 21:58
 */
public interface RetryAction<T> {

    /**
     * 重试操作
     *
     * @param value 对象
     */
    void doAction(T value);
}
