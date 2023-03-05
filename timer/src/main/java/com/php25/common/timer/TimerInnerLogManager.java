package com.php25.common.timer;

import com.php25.common.timer.dao.po.TimerInnerLogPo;

/**
 * @author penghuiping
 * @date 2023/3/5 14:47
 */
public interface TimerInnerLogManager {

    /**
     * 同步(支持并发)执行任务
     *
     * @param task 任务
     */
    void synchronizedExecutionJob(Job task);

    /**
     * 创建定时器内部日志
     *
     * @param timerInnerLogPo 定时器内部日志
     */
    void create(TimerInnerLogPo timerInnerLogPo);

    /**
     * 更新定时器内部日志
     *
     * @param timerInnerLogPo 定时器内部日志
     */
    boolean update(TimerInnerLogPo timerInnerLogPo);
}
