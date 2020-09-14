package com.php25.common.db.repository.shard;

import com.google.common.collect.Lists;
import com.php25.common.db.Db;
import com.php25.common.db.manager.JdbcModelManager;
import com.php25.common.db.repository.BaseDbRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/9/9 16:51
 */
public class BaseShardDbRepositoryImpl<T extends Persistable<ID>, ID> extends JdbcShardDbRepositoryImpl<T, ID> implements BaseDbRepository<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(BaseShardDbRepositoryImpl.class);


    public BaseShardDbRepositoryImpl(List<Db> dbList, ShardRule shardRule) {
        super(dbList, shardRule);
    }

    @NotNull
    @Override
    public <S extends T> S save(S s) {
        ID id = s.getId();
        Db db = shardRule.shardPrimaryKey(this.dbList, id);
        if (s.isNew()) {
            //新增
            db.cndJdbc(model).ignoreCollection(false).insert(s);
        } else {
            //更新
            db.cndJdbc(model).ignoreCollection(false).update(s);
        }
        return s;
    }

    @NotNull
    @Override
    public <S extends T> Iterable<S> saveAll(@NotNull Iterable<S> iterable) {
        AtomicInteger error = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(dbList.size());
        List<LinkedBlockingQueue<Integer>> queues = new ArrayList<>();

        for (Db db : dbList) {
            TransactionTemplate transactionTemplate = db.getJdbcPair().getTransactionTemplate();
            PlatformTransactionManager transactionManager = transactionTemplate.getTransactionManager();
            LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
            queues.add(queue);
            ForkJoinPool.commonPool().submit(() -> {
                TransactionStatus transactionStatus = transactionManager.getTransaction(transactionTemplate);
                try {

                    List<S> models = Lists.newArrayList(iterable).stream()
                            .filter(id -> db.equals(shardRule.shardPrimaryKey(dbList, id)))
                            .collect(Collectors.toList());
                    if (!models.isEmpty()) {
                        S s = models.iterator().next();
                        if (s.isNew()) {
                            db.cndJdbc(model).insertBatch(models);
                        } else {
                            db.cndJdbc(model).updateBatch(models);
                        }
                    }
                } catch (Exception e) {
                    log.error("执行保存操作失败", e);
                    error.addAndGet(1);
                } finally {
                    countDownLatch.countDown();
                }

                try {
                    Integer value = queue.poll(30, TimeUnit.SECONDS);
                    if (value != null && value > 0) {
                        transactionManager.commit(transactionStatus);
                    }
                    //超过30秒还没收到回滚或者提交指令，直接回滚
                    transactionManager.rollback(transactionStatus);
                } catch (InterruptedException e) {
                    //收到中断错误,直接回滚
                    transactionManager.rollback(transactionStatus);
                }
            });
        }

        try {
            boolean result = countDownLatch.await(20, TimeUnit.SECONDS);
            if (result) {
                if (error.get() > 0) {
                    for (LinkedBlockingQueue<Integer> queue : queues) {
                        //回滚
                        queue.offer(0);
                    }
                } else {
                    for (LinkedBlockingQueue<Integer> queue : queues) {
                        //提交
                        queue.offer(1);
                    }
                }
            } else {
                for (LinkedBlockingQueue<Integer> queue : queues) {
                    //回滚
                    queue.offer(0);
                }
            }
        } catch (InterruptedException e) {
            for (LinkedBlockingQueue<Integer> queue : queues) {
                //回滚
                queue.offer(0);
            }
        }
        return iterable;
    }

    @NotNull
    @Override
    public Optional<T> findById(@NotNull ID id) {
        Db db = shardRule.shardPrimaryKey(this.dbList, id);
        T t = db.cndJdbc(model).ignoreCollection(false).whereEq(pkName, id).single();
        if (null != t) {
            return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(@NotNull ID id) {
        Db db = shardRule.shardPrimaryKey(this.dbList, id);
        return db.cndJdbc(model).whereEq(pkName, id).count() > 0;
    }

    @NotNull
    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<>();
        for (Db db : dbList) {
            List<T> tmp = db.cndJdbc(model).select();
            if (null != tmp && !tmp.isEmpty()) {
                result.addAll(tmp);
            }
        }
        return result;
    }

    @NotNull
    @Override
    public Iterable<T> findAllById(@NotNull Iterable<ID> iterable) {
        List<T> result = new ArrayList<>();
        for (Db db : dbList) {
            List<ID> ids = Lists.newArrayList(iterable).stream()
                    .filter(id -> db.hashCode() == shardRule.shardPrimaryKey(dbList, id).hashCode())
                    .collect(Collectors.toList());
            List<T> tmp = db.cndJdbc(model).whereIn(pkName, Lists.newArrayList(ids)).select();
            result.addAll(tmp);
        }
        return result;
    }

    @Override
    public long count() {
        long count = 0L;
        for (Db db : dbList) {
            count = count + db.cndJdbc(model).count();
        }
        return count;
    }

    @Override
    public void deleteById(@NotNull ID id) {
        Db db = shardRule.shardPrimaryKey(this.dbList, id);
        T obj = db.cndJdbc(model).whereEq(pkName, id).single();
        this.delete(obj);
    }

    @Override
    public void delete(T t) {
        Db db = shardRule.shardPrimaryKey(this.dbList, t.getId());
        db.cndJdbc(model).ignoreCollection(false).delete(t);
    }

    @Override
    public void deleteAll(@NotNull Iterable<? extends T> iterable) {
        for (Db db : dbList) {
            List<ID> ids = Lists.newArrayList(iterable).stream()
                    .filter(model -> db.hashCode() == shardRule.shardPrimaryKey(dbList, model.getId()).hashCode())
                    .map(T::getId)
                    .collect(Collectors.toList());
            String pkName = JdbcModelManager.getPrimaryKeyColName(model);
            db.cndJdbc(model).whereIn(pkName, ids).delete();
        }
    }

    @Override
    public void deleteAll() {
        for (Db db : dbList) {
            db.cndJdbc(model).delete();
        }
    }
}
