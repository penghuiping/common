package com.php25.common.timer.manager;


import com.php25.common.timer.Job;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/3/13 09:58
 */
public interface JobManager {

    /**
     * 序列化任务
     *
     * @param job 任务
     */
    void create(Job job);

    /**
     * @param jobId  任务id
     * @param enable 0:无效 1:有效
     */
    void update(String jobId, Integer enable);

    /**
     * 获取所有有效的执行任务
     *
     * @return 任务列表
     */
    List<Job> findAllEnabled();

    /**
     * 判断此任务是否有效
     *
     * @param jobId 任务id
     * @return true:有效 false:无效
     */
    Boolean isEnable(String jobId);
}
