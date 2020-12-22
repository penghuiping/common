package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.db.Db;
import com.php25.common.db.core.manager.JdbcModelManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/1/8 16:37
 */
public class BaseDbRepositoryImpl<T extends Persistable<ID>, ID> extends JdbcDbRepositoryImpl<T, ID> implements BaseDbRepository<T, ID> {

    public BaseDbRepositoryImpl(Db db) {
        super(db);
    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> S save(S s) {
        if (s.isNew()) {
            //新增
            db.getBaseSqlExecute().insert(db.from(model).insert(s));
        } else {
            //更新
            db.getBaseSqlExecute().update(db.from(model).update(s));
        }
        return s;
    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> objs) {
        S s = objs.iterator().next();
        if (s.isNew()) {
            db.getBaseSqlExecute().insertBatch(db.from(model).insertBatch(Lists.newArrayList(objs)));
        } else {
            db.getBaseSqlExecute().updateBatch(db.from(model).updateBatch(Lists.newArrayList(objs)));
        }
        return objs;
    }

    @NotNull
    @Override
    public Optional<T> findById(@NotNull ID id) {
        T t = db.getBaseSqlExecute().single(db.from(model).whereEq(pkName, id).single());
        if (null == t) {
            return Optional.empty();
        } else {
            return Optional.of(t);
        }
    }

    @Override
    public boolean existsById(@NotNull ID id) {
        return db.getBaseSqlExecute().count(db.from(model).whereEq(pkName, id).count()) > 0;
    }

    @NotNull
    @Override
    public Iterable<T> findAll() {
        return db.getBaseSqlExecute().select(db.from(model).select());
    }

    @NotNull
    @Override
    public Iterable<T> findAllById(@NotNull Iterable<ID> ids) {
        return db.getBaseSqlExecute().select(db.from(model).whereIn(pkName, Lists.newArrayList(ids)).select());
    }

    @Override
    public long count() {
        return db.getBaseSqlExecute().count(db.from(model).count());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(@NotNull ID id) {
        T obj = db.getBaseSqlExecute().single(db.from(model).whereEq(pkName, id).single());
        this.delete(obj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(@NotNull T t) {
        db.getBaseSqlExecute().delete(db.from(model).delete(t));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(@NotNull Iterable<? extends T> objs) {
        List<Object> ids = Lists.newArrayList(objs).stream().map(o -> JdbcModelManager.getPrimaryKeyValue(model, o)).collect(Collectors.toList());
        String pkName = JdbcModelManager.getPrimaryKeyColName(model);
        db.getBaseSqlExecute().delete(db.from(model).whereIn(pkName, ids).delete());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        db.getBaseSqlExecute().delete(db.from(model).delete());
    }
}
