package com.php25.common.ws.retry;

/**
 * @author penghuiping
 * @date 2021/8/23 09:09
 */
public class DefaultRetryQueue<T> extends AbstractRetryQueue<T> {


    public DefaultRetryQueue(int maxRetryNumber, long retryInterval, RetryAction<T> retryAction, RejectAction<T> rejectAction) {
        super(maxRetryNumber, retryInterval, retryAction, rejectAction);
    }
}
