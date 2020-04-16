package com.php25.common.coresample;

import com.php25.common.core.util.TimeUtil;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/4/12 23:05
 */
public class TimerTest {

    int t = 0;

    @Test
    public void test() throws Exception {

        TaskTimer taskTimer = new TaskTimer();

        for (int i = 0; i < 100; i++) {
            OnceTask onceTask = new OnceTask();
            onceTask.setRunnable(() -> {
                try {
                    System.out.println(t++);
                    throw new RuntimeException("出错啦");
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            });
            onceTask.setTime("2020-04-12 23:58:00");
            taskTimer.addTask(onceTask);
        }
        taskTimer.run();
    }

    class TaskTimer {
        List<Task> tasks = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(20);

        public void addTask(Task task) {
            this.tasks.add(task);
        }

        public void run() {
            while (true) {
                String time = TimeUtil.getTime(new Date(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                System.out.println(time);

                Iterator<Task> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task instanceof OnceTask) {
                        OnceTask onceTask = (OnceTask) task;
                        if (onceTask.getTime().equals(time)) {
                            executor.submit(onceTask);
                            iterator.remove();
                        }
                    }else if(task instanceof FrequencyTask) {

                    }else {
                        iterator.remove();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Task implements Runnable {


        private Integer status;


        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        private Runnable runnable;


        public Runnable getRunnable() {
            return runnable;
        }

        public void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    class OnceTask extends Task {
        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

    }

    class FrequencyTask extends Task {

        private TimeUnit timeUnit;


    }
}
