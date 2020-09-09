package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.db.Db;
import com.php25.common.db.manager.JdbcModelManager;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> S save(S s) {
        if (s.isNew()) {
            //新增
            db.cndJdbc(model).ignoreCollection(false).insert(s);
        } else {
            //更新
            db.cndJdbc(model).ignoreCollection(false).update(s);
        }
        return s;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> objs) {
        S s = objs.iterator().next();
        if (s.isNew()) {
            db.cndJdbc(model).insertBatch(Lists.newArrayList(objs));
        } else {
            db.cndJdbc(model).updateBatch(Lists.newArrayList(objs));
        }
        return objs;
    }

    @Override
    public Optional<T> findById(ID id) {
        T t = db.cndJdbc(model).ignoreCollection(false).whereEq(pkName, id).single();
        if (null == t) {
            return Optional.empty();
        } else {
            return Optional.of(t);
        }
    }

    @Override
    public boolean existsById(ID id) {
        return db.cndJdbc(model).whereEq(pkName, id).count() > 0;
    }

    @Override
    public Iterable<T> findAll() {
        return db.cndJdbc(model).select();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return db.cndJdbc(model).whereIn(pkName, Lists.newArrayList(ids)).select();
    }

    @Override
    public long count() {
        return db.cndJdbc(model).count();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(ID id) {
        T obj = db.cndJdbc(model).whereEq(pkName, id).single();
        this.delete(obj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T t) {
        db.cndJdbc(model).ignoreCollection(false).delete(t);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Iterable<? extends T> objs) {
        List<Object> ids = Lists.newArrayList(objs).stream().map(o -> JdbcModelManager.getPrimaryKeyValue(model, o)).collect(Collectors.toList());
        String pkName = JdbcModelManager.getPrimaryKeyColName(model);
        db.cndJdbc(model).whereIn(pkName, ids).delete();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        db.cndJdbc(model).delete();
    }
}
