package com.php25.common.ws.retry;

/**
 * 重试队列
 * <p>
 * 1. 向普通队列一样可以把对象加入此队列，符合先进先出
 * 2. 所有加入的此重试队列的对象,重试队列都会记录对象的加入时间
 * 3. 对于加入重试队列时间过长的对象，重试队列都会触发默认的重试操作
 * 4. 判定是否是时间过长对象可以通过阈值设定
 * 5. 重试队列会记录对象的重试次数,重试次数也有上限,可以配置修改
 * 6. 当对象到达重试次数上限以后，对象就会被触发丢弃操作
 * 7. 可以通过对象id，从重试队列中获取对象
 * 8. 也可以通过对象id，把此对象从重试队列中移除
 *
 * @author penghuiping
 * @date 2021/8/22 21:42
 */
public interface RetryQueue<T> {

    /**
     * 把对象加入重试队列
     *
     * @param id    对象id
     * @param value 对象
     * @return true:加入成功
     */
    Boolean offer(String id, T value);

    /**
     * 把对象加入重试队列
     *
     * @param id           对象id
     * @param value        对象
     * @param rejectAction 重试超过阈值的拒绝策略
     * @return true:加入成功
     */
    Boolean offer(String id, T value, RejectAction<T> rejectAction);

    /**
     * 根据对象id获取对象
     *
     * @param id 对象id
     * @return 对象
     */
    T get(String id);


    /**
     * 根据对象id移除对象
     *
     * @param id 对象id
     * @return 对象
     */
    T remove(String id);

    /**
     * 获取重试队列中的对象数量
     *
     * @return
     */
    Long size();
}
