package com.php25.common.db.repository;

import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.PageUtil;
import com.php25.common.db.Db;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.db.manager.JdbcModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author: penghuiping
 * @date: 2019/7/25 15:38
 * @description:
 */
public class JdbcDbRepositoryImpl<T, ID extends Serializable> implements JdbcDbRepository<T, ID> {

    @Autowired
    private Db db;

    private Class model;

    private String pkName;

    public JdbcDbRepositoryImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        model = (Class) params[0];
        pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
    }

    @Override
    public List<T> findAllEnabled() {
        List<T> list = db.cndJdbc(model).whereEq("enable", 1).select();
        return list;
    }

    @Override
    public Optional<T> findByIdEnable(ID id) {
        return Optional.of(db.cndJdbc(model).whereEq(pkName,id).andEq("enable",1).single());
    }

    @Override
    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
        CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
        return Optional.of(cnd.single());
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
        CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
        return cnd.select();
    }

    @Override
    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
        CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
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
        long total = cnd.condition().andSearchParamBuilder(searchParamBuilder).count();
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
        CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
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
        CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
        return cnd.count();
    }
}
