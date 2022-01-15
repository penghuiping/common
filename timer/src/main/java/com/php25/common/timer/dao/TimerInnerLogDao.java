package com.php25.common.timer.dao;

import com.php25.common.timer.po.TimerInnerLogPo;

/**
 * @author penghuiping
 * @date 2022/1/15 13:36
 */
public interface TimerInnerLogDao {

    /**
     * 获取TimerInnerLog
     *
     * @param id            任务id
     * @param executionTime 执行时间
     * @return TimerInnerLog
     */
    TimerInnerLogPo findOneByIdAndExecutionTime(String id, Long executionTime);


    /**
     * 更新TimerInnerLog状态
     *
     * @param status        状态
     * @param id            任务id
     * @param executionTime 执行时间
     * @return true:更新成功
     */
    Boolean updateStatusByIdAndExecutionTime(Integer status, String id, Long executionTime);

    /**
     * 插入一条日志
     *
     * @param timerInnerLog 定时器内部日志
     * @return true: 成功
     */
    Boolean insert(TimerInnerLogPo timerInnerLog);
}
