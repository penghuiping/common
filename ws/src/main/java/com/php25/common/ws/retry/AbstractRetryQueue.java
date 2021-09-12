package com.php25.common.ws.retry;

import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author penghuiping
 * @date 2021/8/22 22:05
 */
public abstract class AbstractRetryQueue<T> implements RetryQueue<T> {

    /**
     * 重试次数上限
     */
    private final int maxRetryNumber;

    /**
     * 重试间隔
     */
    private final long retryInterval;

    /**
     * 内部容器
     */
    private final ConcurrentHashMap<String, RetryObject<T>> container;

    /**
     * 重试策略
     */
    private final RetryAction<T> retryAction;

    /**
     * 拒绝策略
     */
    private final RejectAction<T> rejectAction;

    /**
     * 定时器
     */
    private final Timer timer;

    public AbstractRetryQueue(int maxRetryNumber, long retryInterval, RetryAction<T> retryAction, RejectAction<T> rejectAction) {
        this.maxRetryNumber = maxRetryNumber;
        this.retryInterval = retryInterval;
        this.retryAction = retryAction;
        this.rejectAction = rejectAction;
        container = new ConcurrentHashMap<>(1024);
        timer = new Timer();
    }

    @Override
    public Boolean offer(String id, T value) {
        return this.offer(id, value, null);
    }

    @Override
    public Boolean offer(String id, T value, RejectAction<T> rejectAction) {
        RetryObject<T> retryObject = new RetryObject<>();
        retryObject.setId(id);
        retryObject.setValue(value);
        retryObject.setRetryNumber(0);
        retryObject.setCreateTime(new Date());
        retryObject.setLastModifiedTime(new Date());
        retryObject.setRejectAction(rejectAction);
        container.put(id, retryObject);
        timer.add(new Job(id, retryObject.getLastModifiedTime().getTime() + this.retryInterval, new RetryActionAdapter(retryObject, this.retryAction)));
        return true;
    }

    @Override
    public T get(String id) {
        return this.container.get(id).getValue();
    }

    @Override
    public T remove(String id) {
        timer.stop(id);
        RetryObject<T> retryObject = container.remove(id);
        if (null != retryObject) {
            return retryObject.getValue();
        }
        return null;
    }

    @Override
    public Long size() {
        return (long) container.size();
    }

    private class RetryActionAdapter implements Runnable {
        private final RetryObject<T> retryObject;

        private final RetryAction<T> retryAction;

        public RetryActionAdapter(RetryObject<T> retryObject, RetryAction<T>
                retryAction) {
            this.retryObject = retryObject;
            this.retryAction = retryAction;
        }

        @Override
        public void run() {
            retryObject.setRetryNumber(retryObject.getRetryNumber() + 1);
            retryAction.doAction(this.retryObject.getValue());
            if (retryObject.getRetryNumber() >= AbstractRetryQueue.this.maxRetryNumber) {
                //触发拒绝策略
                AbstractRetryQueue.this.remove(retryObject.getId());
                if (null != retryObject.getRejectAction()) {
                    retryObject.getRejectAction().doAction(this.retryObject.getValue());
                } else {
                    AbstractRetryQueue.this.rejectAction.doAction(this.retryObject.getValue());
                }
            }
            //进行下次的重试
            retryObject.setLastModifiedTime(new Date());
            timer.add(new Job(retryObject.getId(), retryObject.getLastModifiedTime().getTime() + AbstractRetryQueue.this.retryInterval, new RetryActionAdapter(retryObject, this.retryAction)));
        }
    }
}
