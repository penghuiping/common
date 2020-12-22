package com.php25.common.db.repository;

import com.php25.common.core.util.PageUtil;
import com.php25.common.db.Db;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.BaseQuery;
import com.php25.common.db.specification.SearchParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
public class JdbcDbRepositoryImpl<T, ID> implements JdbcDbRepository<T, ID> {

    protected Db db;

    protected Class<?> model;

    protected String pkName;

    public JdbcDbRepositoryImpl(Db db) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.model = (Class<?>) params[0];
        this.pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
        this.db = db;
    }

    @Override
    public List<T> findAllEnabled() {
        return db.getBaseSqlExecute().select(db.from(model).whereEq("enable", 1).select());
    }

    @Override
    public Optional<T> findByIdEnable(ID id) {
        return Optional.of(db.getBaseSqlExecute().single(
                db.from(model).whereEq(pkName, id).andEq("enable", 1).single()));
    }

    @Override
    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
        BaseQuery query = db.from(model).andSearchParamBuilder(searchParamBuilder);
        return Optional.ofNullable(db.getBaseSqlExecute().single(query.single()));
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
        BaseQuery query = db.from(model).andSearchParamBuilder(searchParamBuilder);
        return db.getBaseSqlExecute().select(query.select());
    }

    @Override
    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
        BaseQuery query = db.from(model).andSearchParamBuilder(searchParamBuilder);
        Sort sort = pageable.getSort();
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                query.asc(order.getProperty());
            } else {
                query.desc(order.getProperty());
            }
        }
        int[] page = PageUtil.transToStartEnd(pageable.getPageNumber(), pageable.getPageSize());
        List<T> list = db.getBaseSqlExecute().select(query.limit(page[0], page[1]).select());
        long total = db.getBaseSqlExecute().count(db.from(model).andSearchParamBuilder(searchParamBuilder).count());
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
        BaseQuery query = db.from(model).andSearchParamBuilder(searchParamBuilder);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                query.asc(order.getProperty());
            } else {
                query.desc(order.getProperty());
            }
        }
        return db.getBaseSqlExecute().select(query.select());
    }

    @Override
    public long count(SearchParamBuilder searchParamBuilder) {
        BaseQuery query = db.from(model).andSearchParamBuilder(searchParamBuilder);
        return db.getBaseSqlExecute().count(query.count());
    }
}
