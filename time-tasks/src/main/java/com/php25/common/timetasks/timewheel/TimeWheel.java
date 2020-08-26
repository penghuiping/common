package com.php25.common.timetasks.timewheel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.timetasks.cron.Cron;
import com.php25.common.timetasks.repository.TimeTaskDbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 时间轮
 *
 * @author penghuiping
 * @date 2020/5/14 16:12
 */
public class TimeWheel implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(TimeWheel.class);

    /**
     * 时间轮,类比钟表，一圈3600, 可容纳1小时的数据
     */
    private WheelSlotList[] wheel;


    /**
     * 时间轮运行状态  0:stop 1:run
     */
    private AtomicInteger state = new AtomicInteger(0);

    /**
     * 时间轮槽数
     */
    private int slotNumber;

    /**
     * 头指针，随着秒针移动而变化
     */
    private volatile int head = 0;

    /**
     * 尾指针
     */
    private volatile int tail = 0;


    /**
     * 线程池，用于异步执行工作任务
     */
    private ExecutorService threadPool;

    private TimeTaskDbRepository timeTaskDbRepository;

    public void setTimeTaskDbRepository(TimeTaskDbRepository timeTaskDbRepository) {
        this.timeTaskDbRepository = timeTaskDbRepository;
    }

    public TimeWheel() {
        this(3600);
    }

    public String generateJobId() {
        return timeTaskDbRepository.generateJobId();
    }

    public TimeWheel(int slotSize) {
        slotNumber = slotSize;
        this.wheel = new WheelSlotList[slotNumber];

        for (int i = 0; i < slotNumber; i++) {
            wheel[i] = new WheelSlotList();
        }
    }

    /**
     * 往时间轮中加入工作任务
     *
     * @param timeTask return true:成功 false:失败
     */
    public boolean add(TimeTask timeTask) {
        //放入当前时间轮槽
        LocalDateTime time = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);
        long timeSeconds = time.toEpochSecond(ZoneOffset.of("+8"));
        int length = (int) (timeSeconds - getCurrentTimeStamp());

        int scope = (tail >= head) ? (tail - head) : (tail + slotNumber - head);
        if (length > 0 && length < scope) {
            log.debug("加入wheel，{}", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            WheelSlotList ptr = wheel[(head + length) % slotNumber];
            ptr.add(timeTask);
            return true;
        } else {
            //放入数据库
            log.debug("加入数据库，{}", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            timeTaskDbRepository.save(timeTask);
            return true;
        }
    }

    /**
     * 启动时间轮
     */
    public void start() {
        if (state.get() == 0) {
            head = (int) getCurrentTimeStamp() % slotNumber;
            tail = (head + slotNumber - 1) % slotNumber;

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("TimeTasks-Thread-%d").build();

            this.threadPool = new ThreadPoolExecutor(50, 200,
                    5000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(20000), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

            Thread thread = namedThreadFactory.newThread(() -> {
                state.compareAndSet(0, 1);
                while (state.get() == 1) {
                    final LocalDateTime now = LocalDateTime.now();
                    long nowSeconds = now.toEpochSecond(ZoneOffset.of("+8"));
                    head = (int) nowSeconds % slotNumber;
                    WheelSlotList ptr = wheel[head];

                    int scope = (tail >= head) ? (tail - head) : (tail + slotNumber - head);
                    if (scope == slotNumber / 2) {
                        //运行过半以后，调整tail值，并且加载新的一半数据进入时间轮
                        tail = (tail + slotNumber / 2 - 1) % slotNumber;
                        namedThreadFactory.newThread(() -> {
                            log.debug("触发补数据操作");
                            //加载数据
                            List<TimeTask> timeTasks = timeTaskDbRepository.findByExecuteTimeScope(now.plusSeconds(scope), now.plusSeconds(scope + slotNumber / 2 - 1));
                            log.debug("触发补数据操作,补时间{}-{}", now.plusSeconds(scope).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                    , now.plusSeconds(scope + slotNumber / 2 - 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            if (null != timeTasks && !timeTasks.isEmpty()) {
                                timeTasks.forEach(this::add);
                                timeTaskDbRepository.deleteAll(timeTasks.stream().map(TimeTask::getJobId).collect(Collectors.toList()));
                                timeTaskDbRepository.deleteAllInvalidJob();
                            }
                        }).start();
                    }

                    //运行所有的task
                    Iterator<TimeTask> iterator = ptr.iterator();
                    while (iterator.hasNext()) {
                        TimeTask timeTask = iterator.next();
                        threadPool.submit(() -> {
                            if (null != timeTask.cron && timeTask.cron.length() > 0) {
                                //如果cron可以生成下一次的执行时间
                                LocalDateTime next = Cron.nextExecuteTime(timeTask.cron, now.plusSeconds(1));
                                if (null != next) {
                                    TimeTask timeTask1 = new TimeTask(next, timeTask.task, timeTaskDbRepository.generateJobId(), timeTask.cron);
                                    this.add(timeTask1);
                                }
                            }
                            timeTask.task.run();
                        });
                        iterator.remove();
                    }

                    try {
                        LocalDateTime now1 = LocalDateTime.now();
                        Thread.sleep((999999999 - now1.getNano()) / 1000000, (999999999 - now1.getNano()) % 1000000);
                    } catch (InterruptedException e) {
                        log.error("时间轮睡眠被打断", e);
                    }

                }
            });
            thread.start();
        }
    }


    private long getCurrentTimeStamp() {
        final LocalDateTime now = LocalDateTime.now();
        return now.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 关闭时间轮
     */
    public void stop() {
        if (state.get() != 0) {
            state.compareAndSet(1, 0);
        }
        this.threadPool.shutdown();
        this.threadPool = null;
    }

    @Override
    public void destroy() throws Exception {
        log.info("时间轮销毁...");
        this.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("时间轮开始...");
        this.start();
    }
}
