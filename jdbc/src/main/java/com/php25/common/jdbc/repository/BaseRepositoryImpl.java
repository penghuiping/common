package com.php25.common.jdbc.repository;

import com.google.common.collect.Lists;
import com.php25.common.core.util.PageUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.jdbc.Cnd;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbc.JpaModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by penghuiping on 16/4/4.
 */
@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> implements BaseRepository<T, ID> {

    @Autowired
    private Db db;

    private Class model;

    private String pkName;

    public BaseRepositoryImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        model = (Class) params[0];
        pkName = JpaModelManager.getPrimaryKeyFieldName(model);
    }

    @Override
    public List<T> findAllEnabled() {
        List<T> list = db.cnd(model).whereEq("enable", 1).select();
        return list;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        Cnd cnd = db.cnd(model);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) cnd.asc(order.getProperty());
            else cnd.desc(order.getProperty());
        }
        return cnd.select();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        Sort sort = pageable.getSort();
        Cnd cnd = db.cnd(model);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) cnd.asc(order.getProperty());
            else cnd.desc(order.getProperty());
        }
        int[] page = PageUtil.transToStartEnd(pageable.getPageNumber(), pageable.getPageSize());
        List<T> list = cnd.limit(page[0], page[1]).select();
        long total = cnd.condition().count();
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public <S extends T> S save(S s) {
        int rows = db.cnd(model).insert(s);
        if (rows > 0) {
            return s;
        } else {
            throw new RuntimeException("保存数据库对象失败");
        }
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> iterable) {
        int[] rows = db.cnd(model).insertBatch(Lists.newArrayList(iterable));
        for (int row : rows) {
            if (row <= 0) {
                throw new RuntimeException("批量保存数据库对象失败");
            }
        }
        return iterable;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(db.cnd(model).whereEq(pkName, id).single());
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        return db.cnd(model).select();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        return db.cnd(model).whereIn(pkName, Lists.newArrayList(iterable)).select();
    }

    @Override
    public long count() {
        return db.cnd(model).count();
    }

    @Override
    public void deleteById(ID id) {
        db.cnd(model).whereEq(pkName, id).delete();
    }

    @Override
    public void delete(T t) {
        try {
            ID id = (ID) ReflectUtil.getMethod(model, "get" + pkName.substring(0, 1).toUpperCase() + pkName.substring(1, pkName.length())).invoke(t);
            db.cnd(model).whereEq(JpaModelManager.getPrimaryKeyFieldName(model), id).delete();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("无法获取" + model.getSimpleName() + "主键id的值", e);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        List<ID> list = new ArrayList<>();
        Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            try {
                T t = it.next();
                ID id = (ID) ReflectUtil.getMethod(model, "get" + pkName.substring(0, 1).toUpperCase()
                        + pkName.substring(1, pkName.length())).invoke(t);
                list.add(id);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("无法获取" + model.getSimpleName() + "主键id的值", e);
            }
        }
        db.cnd(model).whereIn(pkName, list).delete();
    }

    @Override
    public void deleteAll() {
        db.cnd(model).delete();
    }
}
