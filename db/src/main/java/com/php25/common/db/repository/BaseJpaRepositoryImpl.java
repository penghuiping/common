package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.PageUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.cnd.CndJpa;
import com.php25.common.db.Db;
import com.php25.common.db.manager.JpaModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * database Repository层的基础实现类
 *
 * @author penghuiping
 * @date 2018-04-04
 */
@NoRepositoryBean
public class BaseJpaRepositoryImpl<T, ID extends Serializable> implements BaseRepository<T, ID> {

    @Autowired
    private Db db;

    private Class model;

    private String pkName;

    public BaseJpaRepositoryImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        model = (Class) params[0];
        pkName = JpaModelManager.getPrimaryKeyFieldName(model);
    }

    @Override
    public List<T> findAllEnabled() {
        List<T> list = db.cndJpa(model).whereEq("enable", 1).select();
        return list;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        CndJpa cnd = db.cndJpa(model);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                cnd.asc(order.getProperty());
            } else {
                cnd.desc(order.getProperty());
            }
        }
        return cnd.select();
    }


    @Override
    public Page<T> findAll(Pageable pageable) {
        Sort sort = pageable.getSort();
        CndJpa cnd = db.cndJpa(model);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                cnd.asc(order.getProperty());
            } else {
                cnd.desc(order.getProperty());
            }
        }
        int[] page = PageUtil.transToStartEnd(pageable.getPageNumber(), pageable.getPageSize());
        List<T> list = cnd.limit(page[0], page[1]).select();
        long total = cnd.condition().count();
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public <S extends T> S save(S s) {
        try {
            ID id = (ID) ReflectUtil.getMethod(model, "get" + StringUtil.capitalizeFirstLetter(pkName)).invoke(s);
            Optional<T> tmp = findById(id);
            if (tmp.isPresent()) {
                //update
                db.cndJpa(model).update(s);
                return s;
            } else {
                //insert
                db.cndJpa(model).insert(s);
                return s;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("无法获取" + model.getSimpleName() + "主键id的值", e);
        }
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> iterable) {
        //判断是保存还是更新
        List<S> lists = Lists.newArrayList(iterable);
        S s = lists.get(0);
        ID id = null;
        try {
            id = (ID) ReflectUtil.getMethod(model, "get" + StringUtil.capitalizeFirstLetter(pkName)).invoke(s);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("无法获取" + model.getSimpleName() + "主键id的值", e);
        }
        Optional<T> tmp = findById(id);
        if (tmp.isPresent()) {
            //更新操作
            int[] rows = db.cndJpa(model).updateBatch(lists);
            for (int row : rows) {
                if (row <= 0) {
                    throw new RuntimeException("批量更新数据库对象失败");
                }
            }
            return iterable;
        } else {
            int[] rows = db.cndJpa(model).insertBatch(lists);
            for (int row : rows) {
                if (row <= 0) {
                    throw new RuntimeException("批量保存数据库对象失败");
                }
            }
            return iterable;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (null == id) {
            return Optional.empty();
        }
        return Optional.ofNullable(db.cndJpa(model).whereEq(pkName, id).single());
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        return db.cndJpa(model).select();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        return db.cndJpa(model).whereIn(pkName, Lists.newArrayList(iterable)).select();
    }

    @Override
    public long count() {
        return db.cndJpa(model).count();
    }

    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, "id不能为null");
        db.cndJpa(model).whereEq(pkName, id).delete();
    }

    @Override
    public void delete(T t) {
        try {
            ID id = (ID) ReflectUtil.getMethod(model, "get" + StringUtil.capitalizeFirstLetter(pkName)).invoke(t);
            db.cndJpa(model).whereEq(JpaModelManager.getPrimaryKeyFieldName(model), id).delete();
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
                ID id = (ID) ReflectUtil.getMethod(model, "get" + StringUtil.capitalizeFirstLetter(pkName)).invoke(t);
                list.add(id);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("无法获取" + model.getSimpleName() + "主键id的值", e);
            }
        }
        db.cndJpa(model).whereIn(pkName, list).delete();
    }

    @Override
    public void deleteAll() {
        db.cndJpa(model).delete();
    }

    @Override
    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
        CndJpa cnd = db.cndJpa(model).andSearchParamBuilder(searchParamBuilder);
        return Optional.of(cnd.single());
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
        CndJpa cnd = db.cndJpa(model).andSearchParamBuilder(searchParamBuilder);
        return cnd.select();
    }

    @Override
    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
        CndJpa cnd = db.cndJpa(model).andSearchParamBuilder(searchParamBuilder);
        Sort sort = pageable.getSort();
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                cnd.asc(order.getProperty());
            } else {
                cnd.desc(order.getProperty());
            }
        }
        int[] page = PageUtil.transToStartEnd(pageable.getPageNumber(), pageable.getPageSize());
        List<T> list = cnd.limit(page[0], page[1]).select();
        long total = cnd.condition().count();
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
        CndJpa cnd = db.cndJpa(model).andSearchParamBuilder(searchParamBuilder);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                cnd.asc(order.getProperty());
            } else {
                cnd.desc(order.getProperty());
            }
        }
        return cnd.select();
    }

    @Override
    public long count(SearchParamBuilder searchParamBuilder) {
        CndJpa cnd = db.cndJpa(model).andSearchParamBuilder(searchParamBuilder);
        return cnd.count();
    }
}
