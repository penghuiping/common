package com.php25.common.db.repository.shard;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 二阶段提交事务
 *
 * @author penghuiping
 * @date 2020/9/24 13:51
 */
public class TwoPhaseCommitTransaction {

    private static final Logger log = LoggerFactory.getLogger(TwoPhaseCommitTransaction.class);

    private static final int timeout = 20;

    private final ExecutorService executor;

    public TwoPhaseCommitTransaction() {
        this.executor = new ThreadPoolExecutor(0, 20,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("tow-phase-commit-transaction-%d")
                .build());
    }

    public <T> List<T> execute(TransactionCallback<T>... transactionCallbacks) {
        return this.execute(Lists.newArrayList(transactionCallbacks));
    }

    public <T> List<T> execute(List<TransactionCallback<T>> transactionCallbacks) {
        AtomicInteger error = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(transactionCallbacks.size());
        List<LinkedBlockingQueue<Integer>> queues = new ArrayList<>();
        List<Future<T>> futures = new ArrayList<>();

        //分发任务
        for (TransactionCallback<T> callback : transactionCallbacks) {
            TransactionTemplate transactionTemplate = callback.getTransactionTemplate();
            PlatformTransactionManager transactionManager = transactionTemplate.getTransactionManager();
            LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            queues.add(queue);
            Future<T> future = executor.submit(() -> {
                TransactionStatus transactionStatus = transactionManager.getTransaction(transactionTemplate);
                T result = null;
                try {
                    result = callback.doInTransaction();
                } catch (Exception e) {
                    log.error("执行保存操作失败", e);
                    error.addAndGet(1);
                } finally {
                    countDownLatch.countDown();
                }
                try {
                    Integer value = queue.poll(timeout, TimeUnit.SECONDS);
                    if (value != null && value > 0) {
                        transactionManager.commit(transactionStatus);
                        return result;
                    }
                } catch (InterruptedException e) {
                    log.error("出错啦!", e);
                }
                //超过30秒还没收到回滚或者提交指令，直接回滚
                transactionManager.rollback(transactionStatus);
                return result;
            });
            futures.add(future);
        }

        //判断是否全部执行成功，成功提交事务，否则回滚
        try {
            boolean result = countDownLatch.await(timeout, TimeUnit.SECONDS);
            if (result) {
                if (error.get() <= 0) {
                    for (LinkedBlockingQueue<Integer> queue : queues) {
                        //提交
                        queue.offer(1);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("出错啦!", e);
        }

        for (LinkedBlockingQueue<Integer> queue : queues) {
            //回滚
            queue.offer(0);
        }

        //合并执行结果集
        return futures.stream().map(tFuture -> {
            T result = null;
            try {
                result = tFuture.get();
            } catch (Exception e) {
                log.error("出错啦!", e);
            }
            return result;
        }).collect(Collectors.toList());

    }
}
