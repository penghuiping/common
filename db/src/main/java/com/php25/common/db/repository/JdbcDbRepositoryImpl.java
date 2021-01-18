package com.php25.common.db.repository;

import com.php25.common.core.util.PageUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.BaseQuery;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.specification.SearchParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

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

    protected JdbcTemplate jdbcTemplate;

    protected DbType dbType;

    protected Class<?> model;

    protected String pkName;

    public JdbcDbRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.model = (Class<?>) params[0];
        this.pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
        this.jdbcTemplate = jdbcTemplate;
        this.dbType = dbType;
    }

    @Override
    public List<T> findAllEnabled() {
        SqlParams sqlParams = Queries.of(this.dbType).from(model).whereEq("enable", 1).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public Optional<T> findByIdEnable(ID id) {
        SqlParams sqlParams = Queries.of(dbType).from(model).whereEq(pkName, id).andEq("enable", 1).single();
        T result = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
        SqlParams sqlParams = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder).single();
        T result = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
        return Optional.ofNullable(result);
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
        SqlParams sqlParams = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
        BaseQuery query = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder);
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
        SqlParams sqlParams = query.limit(page[0], page[1]).select();
        List<T> list = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
        SqlParams sqlParams1 = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder).count();
        long total = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).count(sqlParams1);
        return new PageImpl<T>(list, pageable, total);
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
        BaseQuery query = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder);
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            if (order.getDirection().isAscending()) {
                query.asc(order.getProperty());
            } else {
                query.desc(order.getProperty());
            }
        }
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(query.select());
    }

    @Override
    public long count(SearchParamBuilder searchParamBuilder) {
        BaseQuery query = Queries.of(dbType).from(model).andSearchParamBuilder(searchParamBuilder);
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).count(query.count());
    }
}
