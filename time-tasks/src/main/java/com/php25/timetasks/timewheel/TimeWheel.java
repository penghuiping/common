package com.php25.timetasks.timewheel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间轮
 *
 * @author penghuiping
 * @date 2020/5/14 16:12
 */
public class TimeWheel {

    private static final Logger log = LoggerFactory.getLogger(TimeWheel.class);

    /**
     * 时间轮,类比钟表，一圈60, 可容纳1天的数据
     */
    private WheelSlotList[] wheel;

    /**
     * 下一轮的时间轮
     */
    private WheelSlotList[] nextWheel;

    /**
     * 时间轮运行状态  0:stop 1:run
     */
    private AtomicInteger state = new AtomicInteger(0);

    /**
     * 时间轮槽数
     */
    private int slotNumber;

    /**
     * 时间轮范围
     */
    private RoundScope scope;


    /**
     * 线程池，用于异步执行工作任务
     */
    private ExecutorService threadPool;

    public TimeWheel(RoundScope roundScope) {
        this.scope = roundScope;
        switch (this.scope) {
            case DAY:
                slotNumber = 60 * 60 * 24;
                break;
            case HOUR:
                slotNumber = 60 * 60;
                break;
            case MINUTE:
                slotNumber = 60;
                break;
            default:
                break;
        }

        this.wheel = new WheelSlotList[slotNumber];
        this.nextWheel = new WheelSlotList[slotNumber];

        for (int i = 0; i < slotNumber; i++) {
            wheel[i] = new WheelSlotList();
            nextWheel[i] = new WheelSlotList();
        }
    }

    /**
     * 往时间轮中加入工作任务
     *
     * @param timeTask return true:成功 false:失败
     */
    public boolean add(TimeTask timeTask) {
        if (isTaskInCurrentRound(timeTask)) {
            //放入当前时间轮槽
            LocalDateTime time = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);
            int slot = getWheelSlotByTime(time, this.scope);
            WheelSlotList ptr = wheel[slot];
            ptr.add(timeTask);
            return true;
        }

        if (isTaskInNextRound(timeTask)) {
            //放入下一轮时间轮槽
            LocalDateTime time = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);
            int slot = getWheelSlotByTime(time, this.scope);
            WheelSlotList ptr = nextWheel[slot];
            ptr.add(timeTask);
            return true;
        }
        return false;
    }

    /**
     * 启动时间轮
     */
    public void start() {
        if (state.get() == 0) {
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("TimeTasks-Thread-%d").build();

            this.threadPool = new ThreadPoolExecutor(50, 200,
                    5000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(20000), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

            Thread thread = namedThreadFactory.newThread(() -> {
                state.compareAndSet(0, 1);
                while (state.get() == 1) {
                    LocalDateTime now = LocalDateTime.now();
                    int slot = getWheelSlotByTime(now, this.scope);
                    WheelSlotList ptr = wheel[slot];

                    //当时间轮运行到最后一个槽位时，替换为下一个时间轮
                    if (slot == wheel.length - 1) {
                        WheelSlotList[] tmp = wheel;
                        wheel = nextWheel;
                        nextWheel = tmp;
                    }

                    //运行所有的task
                    Iterator<TimeTask> iterator = ptr.iterator();
                    while (iterator.hasNext()) {
                        TimeTask timeTask = iterator.next();
                        threadPool.submit(timeTask.task);
                        iterator.remove();
                    }

                    now = LocalDateTime.now();
                    try {
                        Thread.sleep((999999999 - now.getNano()) / 1000000,(999999999 - now.getNano()) % 1000000);

                    } catch (InterruptedException e) {
                        log.error("时间轮睡眠被打断", e);
                    }

                }
            });
            thread.start();
        }
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

    /**
     * 根据当前时间，计算时间轮槽的位置
     *
     * @param now 当前时间
     * @return
     */
    private int getWheelSlotByTime(LocalDateTime now, RoundScope roundScope) {
        int result = 0;
        switch (roundScope) {
            case MINUTE:
                result = now.getSecond();
                break;
            case HOUR:
                result = now.getMinute() * 60 + now.getSecond();
                break;
            case DAY:
                result = now.getHour() * 60 * 60 + now.getMinute() * 60 + now.getSecond();
                break;
            default:
                break;
        }
        return result;
    }


    /**
     * 判断任务是否是当前时间轮
     *
     * @param timeTask
     * @return
     */
    private boolean isTaskInCurrentRound(TimeTask timeTask) {
        LocalDateTime taskTime = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);

        LocalDateTime now = LocalDateTime.now();
        if (now.getSecond() == 59) {
            now = now.plusSeconds(1);
        }
        LocalDateTime start = null;
        LocalDateTime end = null;
        switch (scope) {
            case MINUTE: {
                start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0);
                LocalDateTime next = now.plusMinutes(1);
                end = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), next.getHour(), next.getMinute(), 0);
                break;
            }
            case HOUR: {
                start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
                LocalDateTime next = now.plusHours(1);
                end = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), next.getHour(), 0, 0);
                break;
            }
            case DAY: {
                start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
                LocalDateTime next = now.plusDays(1);
                end = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), 0, 0, 0);
                break;
            }
            default:
                break;
        }

        if ((taskTime.isEqual(start) || taskTime.isAfter(start)) && taskTime.isBefore(end)) {
            return true;
        }


        return false;
    }

    /**
     * 判断任务是否是下一个时间轮
     *
     * @param timeTask
     * @return
     */
    private boolean isTaskInNextRound(TimeTask timeTask) {
        LocalDateTime taskTime = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = null;
        LocalDateTime end = null;
        switch (scope) {
            case MINUTE: {

                LocalDateTime next = now.plusMinutes(1);
                start = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), next.getHour(), next.getMinute(), 0);

                LocalDateTime next1 = now.plusMinutes(2);
                end = LocalDateTime.of(next1.getYear(), next1.getMonth(), next1.getDayOfMonth(), next1.getHour(), next1.getMinute(), 0);
                break;
            }
            case HOUR: {
                LocalDateTime next = now.plusHours(1);
                start = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), next.getHour(), 0, 0);

                LocalDateTime next1 = now.plusHours(2);
                end = LocalDateTime.of(next1.getYear(), next1.getMonth(), next1.getDayOfMonth(), next1.getHour(), 0, 0);
                break;
            }
            case DAY: {
                LocalDateTime next = now.plusDays(1);
                start = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), 0, 0, 0);

                LocalDateTime next1 = now.plusDays(2);
                end = LocalDateTime.of(next1.getYear(), next1.getMonth(), next1.getDayOfMonth(), 0, 0, 0);
                break;
            }
            default:
                break;
        }

        if ((taskTime.isEqual(start) || taskTime.isAfter(start)) && taskTime.isBefore(end)) {
            return true;
        }
        return false;
    }


}
