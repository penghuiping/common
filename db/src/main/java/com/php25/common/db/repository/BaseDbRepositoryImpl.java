package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.db.Db;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author penghuiping
 * @date 2020/1/8 16:37
 */
public class BaseDbRepositoryImpl<T extends Persistable<ID>, ID> extends JdbcDbRepositoryImpl<T, ID> implements CrudRepository<T, ID>, JdbcDbRepository<T, ID> {

    @Autowired
    private Db db;

    private Class model;

    private String pkName;

    public BaseDbRepositoryImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        model = (Class) params[0];
        pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
    }

    @Override
    public <S extends T> S save(S s) {
        if (s.isNew()) {
            //新增
            db.cndJdbc(model).insert(s);
        } else {
            //更新
            db.cndJdbc(model).update(s);
        }
        return s;
    }

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
        T t = db.cndJdbc(model).whereEq(pkName, id).single();
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
    public void deleteById(ID id) {
        db.cndJdbc(model).whereEq(pkName, id).delete();
    }

    @Override
    public void delete(T t) {
        List<ImmutablePair<String, Object>> list = JdbcModelManager.getTableColumnNameAndValue(t, true);
        Object value = null;
        for (ImmutablePair<String, Object> pair : list) {
            if (pkName.equals(pair.getLeft())) {
                value = pair.getRight();
                break;
            }
        }
        db.cndJdbc(model).whereEq(pkName, value).delete();
    }

    @Override
    public void deleteAll(Iterable<? extends T> objs) {
        List<Object> values = new ArrayList<>();
        objs.forEach(obj -> {
            List<ImmutablePair<String, Object>> list = JdbcModelManager.getTableColumnNameAndValue(obj, true);
            Object value = null;
            for (ImmutablePair<String, Object> pair : list) {
                if (pkName.equals(pair.getLeft())) {
                    value = pair.getRight();
                    break;
                }
            }
            if (value != null) {
                values.add(value);
            }
        });
        db.cndJdbc(model).whereIn(pkName, values).delete();
    }

    @Override
    public void deleteAll() {
        db.cndJdbc(model).delete();
    }
}
