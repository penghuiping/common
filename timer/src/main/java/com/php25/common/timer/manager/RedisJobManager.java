package com.php25.common.timer.manager;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.mq.MessageSubscriber;
import com.php25.common.mq.redis.RedisMessageSubscriber;
import com.php25.common.redis.RedisManager;
import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;
import com.php25.common.timer.model.JobModel;
import com.php25.common.timer.repository.JobModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author penghuiping
 * @date 2021/3/13 12:00
 */
@Log4j2
@RequiredArgsConstructor
public class RedisJobManager implements JobManager, InitializingBean {


    private final JobModelRepository jobModelRepository;

    private final MessageQueueManager messageQueueManager;

    private final RedisManager redisManager;

    @Qualifier("mqWorkerQueue")
    private final ExecutorService pool;

    @Value("${server.id}")
    private String serverId;


    @Override
    public void afterPropertiesSet() throws Exception {
        Timer timer = SpringContextHolder.getBean0(Timer.class);
        MessageSubscriber messageSubscriber = new RedisMessageSubscriber(pool, redisManager);
        messageSubscriber.setHandler(message -> {
            JobModel jobModel = (JobModel) message.getBody();
            String className = jobModel.getClassName();
            Runnable task = null;
            try {
                task = (Runnable) Class.forName(className).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.error("定时任务对应的执行代码类加载出错", e);
            }
            Job job = new Job(jobModel.getId(), jobModel.getCron(), task);
            if (jobModel.getEnable() == 1) {
                timer.stop(job.getJobId());
                timer.add(job);
            } else {
                timer.stop(job.getJobId());
            }
        });
        this.messageQueueManager.subscribe("timer_job"
                , this.serverId
                , messageSubscriber);
    }

    /**
     * 序列化任务
     *
     * @param job 任务
     */
    @Override
    public void create(Job job) {
        JobModel jobModel = new JobModel();
        jobModel.setCron(job.getCron());
        jobModel.setId(job.getJobId());
        jobModel.setEnable(1);
        jobModel.setClassName(job.getTask().getClass().getName());
        this.jobModelRepository.create(jobModel);
        Message message = new Message(job.getJobId(), "timer_job", jobModel);
        this.messageQueueManager.send("timer_job", message);
    }

    /**
     * @param jobId  任务id
     * @param enable 0:无效 1:有效
     */
    @Override
    public void update(String jobId, Integer enable) {
        AssertUtil.hasText(jobId, "jobId不能为空");
        JobModel jobModel = new JobModel();
        jobModel.setId(jobId);
        jobModel.setEnable(enable);
        this.jobModelRepository.update(jobModel);

        jobModel = this.jobModelRepository.findById(jobId);
        Message message = new Message(jobId, "timer_job", jobModel);
        this.messageQueueManager.send("timer_job", message);
    }

    /**
     * 获取所有有效的执行任务
     *
     * @return 任务列表
     */
    @Override
    public List<Job> findAllEnabled() {
        List<JobModel> jobModels = this.jobModelRepository.findAllEnabled();
        List<Job> jobs = new ArrayList<>();
        if (null != jobModels && !jobModels.isEmpty()) {

            for (JobModel jobModel : jobModels) {
                String className = jobModel.getClassName();
                Runnable task = null;
                try {
                    task = (Runnable) Class.forName(className).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.error("定时任务对应的执行代码类加载出错", e);
                    continue;
                }
                Job job = new Job(jobModel.getId(), jobModel.getCron(), task);
                jobs.add(job);
            }
        }
        return jobs;
    }

    /**
     * 判断此任务是否有效
     *
     * @param jobId 任务id
     * @return true:有效 false:无效
     */
    @Override
    public Boolean isEnable(String jobId) {
        JobModel jobModel = this.jobModelRepository.findById(jobId);
        return null != jobModel && jobModel.getEnable() == 1;
    }
}
