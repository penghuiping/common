package com.php25.timetasks;

import com.php25.timetasks.cron.Cron;
import com.php25.timetasks.timewheel.RoundScope;
import com.php25.timetasks.timewheel.TimeTask;
import com.php25.timetasks.timewheel.TimeWheel;

import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/6/9 14:27
 */
public class TimeTasks {

    public static TimeWheel startTimeWheel() {
        TimeWheel timeWheel = new TimeWheel(RoundScope.MINUTE);
        timeWheel.start();
        return timeWheel;
    }

    public static void submit(TimeWheel timeWheel, String cron, Runnable task) {
        execute0(timeWheel, cron, task, 2);
    }

    private static void execute0(TimeWheel timeWheel, String cron, Runnable task, int slowTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = Cron.nextExecuteTime(cron, now.plusSeconds(slowTime));
        if(null != next) {
            TimeTask timeTask3 = new TimeTask(next.getYear(), next.getMonthValue(), next.getDayOfMonth()
                    , next.getHour(), next.getMinute(), next.getSecond(), () -> {
                execute0(timeWheel, cron, task, 1);
                task.run();
            });
            timeWheel.add(timeTask3);
        }
    }
}
