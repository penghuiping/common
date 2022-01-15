package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.core.sql.column.Columns;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.php25.common.db.core.sql.column.Columns.col;

/**
 * @author penghuiping
 * @date 2020/1/8 16:37
 */
public class BaseDbRepositoryImpl<T extends Persistable<ID>, ID> extends JdbcDbRepositoryImpl<T, ID> implements BaseDbRepository<T, ID> {

    public BaseDbRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        super(jdbcTemplate, dbType);
    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> S save(S s) {
        if (s.isNew()) {
            //新增
            SqlParams sqlParams = Queries.of(dbType).from(model).insert(s);
            QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insert(sqlParams);
        } else {
            //更新
            SqlParams sqlParams = Queries.of(dbType).from(model).update(s);
            QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).update(sqlParams);
        }
        return s;
    }

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> objs) {
        S s = objs.iterator().next();
        if (s.isNew()) {
            SqlParams sqlParams = Queries.of(dbType).from(model).insertBatch(Lists.newArrayList(objs));
            QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insertBatch(sqlParams);
        } else {
            SqlParams sqlParams = Queries.of(dbType).from(model).updateBatch(Lists.newArrayList(objs));
            QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).updateBatch(sqlParams);
        }
        return objs;
    }

    @NotNull
    @Override
    public Optional<T> findById(@NotNull ID id) {
        SqlParams sqlParams = Queries.of(dbType).from(model).whereEq(Columns.col(pkName), id).single();
        T t = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
        if (null == t) {
            return Optional.empty();
        } else {
            return Optional.of(t);
        }
    }

    @Override
    public boolean existsById(@NotNull ID id) {
        SqlParams sqlParams = Queries.of(dbType).from(model).whereEq(col(pkName), id).count();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).count(sqlParams) > 0;
    }

    @NotNull
    @Override
    public Iterable<T> findAll() {
        SqlParams sqlParams = Queries.of(dbType).from(model).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @NotNull
    @Override
    public Iterable<T> findAllById(@NotNull Iterable<ID> ids) {
        SqlParams sqlParams = Queries.of(dbType).from(model).whereIn(col(pkName), Lists.newArrayList(ids)).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public long count() {
        SqlParams sqlParams = Queries.of(dbType).from(model).count();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).count(sqlParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(@NotNull ID id) {
        SqlParams sqlParams = Queries.of(dbType).from(model).whereEq(col(pkName), id).single();
        T obj = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
        this.delete(obj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(@NotNull T t) {
        SqlParams sqlParams = Queries.of(dbType).from(model).delete(t);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllById(Iterable<? extends ID> ids) {
        String pkName = JdbcModelCacheManager.getPrimaryKeyColName(model);
        SqlParams sqlParams = Queries.of(dbType).from(model).whereIn(col(pkName), Lists.newArrayList(ids)).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(@NotNull Iterable<? extends T> objs) {
        List<Object> ids = Lists.newArrayList(objs).stream().map(o -> JdbcModelCacheManager.getPrimaryKeyValue(model, o)).collect(Collectors.toList());
        String pkName = JdbcModelCacheManager.getPrimaryKeyColName(model);
        SqlParams sqlParams = Queries.of(dbType).from(model).whereIn(col(pkName), ids).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        SqlParams sqlParams = Queries.of(dbType).from(model).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }
}
