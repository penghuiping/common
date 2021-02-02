//package com.php25.common.db.repository.shard;
//
//import com.google.common.collect.Lists;
//import com.php25.common.db.EntitiesScan;
//import com.php25.common.db.Queries;
//import com.php25.common.db.QueriesExecute;
//import com.php25.common.db.core.manager.JdbcModelManager;
//import com.php25.common.db.core.shard.ShardRule;
//import com.php25.common.db.core.sql.SqlParams;
//import com.php25.common.db.repository.BaseDbRepository;
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Persistable;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * @author penghuiping
// * @date 2020/9/9 16:51
// */
//public class BaseShardDbRepositoryImpl<T extends Persistable<ID>, ID extends Comparable<ID>> extends JdbcShardDbRepositoryImpl<T, ID> implements BaseDbRepository<T, ID> {
//
//    private static final Logger log = LoggerFactory.getLogger(BaseShardDbRepositoryImpl.class);
//
//    private final TwoPhaseCommitTransaction twoPhaseCommitTransaction;
//
//
//
//    @NotNull
//    @Override
//    public <S extends T> S save(S s) {
//        ID id = s.getId();
//        if (s.isNew()) {
//            //新增
//            SqlParams sqlParams = Queries.of()
//            db.getBaseSqlExecute().insert(db.from(model).insert(s));
//        } else {
//            //更新
//            db.getBaseSqlExecute().update(db.from(model).update(s));
//        }
//        return s;
//    }
//
//    @NotNull
//    @Override
//    public <S extends T> Iterable<S> saveAll(@NotNull Iterable<S> iterable) {
//        List<TransactionCallback<List<S>>> list = new ArrayList<>();
//        Lists.newArrayList(iterable).stream()
//                .collect(Collectors.groupingBy(s -> shardRule.shard(dbList, s.getId())))
//                .forEach((db, models) -> {
//                    TransactionCallback<List<S>> tmp = new TransactionCallback<>() {
//                        @Override
//                        public List<S> doInTransaction() {
//                            if (!models.isEmpty()) {
//                                S s = models.iterator().next();
//                                if (s.isNew()) {
//                                    db.getBaseSqlExecute().insertBatch(db.from(model).insertBatch(models));
//                                } else {
//                                    db.getBaseSqlExecute().updateBatch(db.from(model).updateBatch(models));
//                                }
//                            }
//                            return models;
//                        }
//
//                        @Override
//                        public EntitiesScan getDb() {
//                            return db;
//                        }
//                    };
//                    list.add(tmp);
//                });
//
//        List<List<S>> result = twoPhaseCommitTransaction.execute(list);
//        return result.stream().flatMap(Collection::stream).collect(Collectors.toList());
//    }
//
//    @NotNull
//    @Override
//    public Optional<T> findById(@NotNull ID id) {
//        EntitiesScan db = shardRule.shard(this.dbList, id);
//        T t = db.getBaseSqlExecute().single(db.from(model).whereEq(pkName, id).single());
//        if (null != t) {
//            return Optional.of(t);
//        }
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean existsById(@NotNull ID id) {
//        EntitiesScan db = shardRule.shard(this.dbList, id);
//        return db.getBaseSqlExecute().count(db.from(model).whereEq(pkName, id).count()) > 0;
//    }
//
//    @NotNull
//    @Override
//    public Iterable<T> findAll() {
//        List<T> result = new ArrayList<>();
//        for (EntitiesScan db : dbList) {
//            List<T> tmp = db.getBaseSqlExecute().select(db.from(model).select());
//            if (null != tmp && !tmp.isEmpty()) {
//                result.addAll(tmp);
//            }
//        }
//        return result;
//    }
//
//    @NotNull
//    @Override
//    public Iterable<T> findAllById(@NotNull Iterable<ID> iterable) {
//        List<T> result = new ArrayList<>();
//        Lists.newArrayList(iterable).stream()
//                .collect(Collectors.groupingBy(id -> shardRule.shard(dbList, id)))
//                .forEach((db, ids) -> {
//                    List<T> tmp = db.getBaseSqlExecute().select(db.from(model).whereIn(pkName, Lists.newArrayList(ids)).select());
//                    result.addAll(tmp);
//                });
//        return result;
//    }
//
//    @Override
//    public long count() {
//        long count = 0L;
//        for (EntitiesScan db : dbList) {
//            count = count + db.getBaseSqlExecute().count(db.from(model).count());
//        }
//        return count;
//    }
//
//    @Override
//    public void deleteById(@NotNull ID id) {
//        EntitiesScan db = shardRule.shard(this.dbList, id);
//        T obj = db.getBaseSqlExecute().single(db.from(model).whereEq(pkName, id).single());
//        this.delete(obj);
//    }
//
//    @Override
//    public void delete(T t) {
//        EntitiesScan db = shardRule.shard(this.dbList, t.getId());
//        db.getBaseSqlExecute().delete(db.from(model).delete(t));
//    }
//
//    @Override
//    public void deleteAll(@NotNull Iterable<? extends T> iterable) {
//        String pkName = JdbcModelManager.getPrimaryKeyColName(model);
//        List<TransactionCallback<Void>> list1 = new ArrayList<>();
//        Lists.newArrayList(iterable).stream()
//                .collect(Collectors.groupingBy(s -> shardRule.shard(dbList, s.getId())))
//                .forEach((db, list) -> {
//                    TransactionCallback<Void> tmp = new TransactionCallback<Void>() {
//                        @Override
//                        public Void doInTransaction() {
//                            List<ID> ids = list.stream().map(Persistable::getId).collect(Collectors.toList());
//                            db.getBaseSqlExecute().delete(db.from(model).whereIn(pkName, ids).delete());
//                            return null;
//                        }
//
//                        @Override
//                        public EntitiesScan getDb() {
//                            return db;
//                        }
//                    };
//                    list1.add(tmp);
//                });
//        twoPhaseCommitTransaction.execute(list1);
//    }
//
//    @Override
//    public void deleteAll() {
//        List<TransactionCallback<Void>> list = new ArrayList<>();
//        for (EntitiesScan db : dbList) {
//            TransactionCallback<Void> tmp = new TransactionCallback<>() {
//                @Override
//                public Void doInTransaction() {
//                    db.getBaseSqlExecute().delete(db.from(model).delete());
//                    return null;
//                }
//
//                @Override
//                public EntitiesScan getDb() {
//                    return db;
//                }
//            };
//            list.add(tmp);
//        }
//        twoPhaseCommitTransaction.execute(list);
//    }
//}
