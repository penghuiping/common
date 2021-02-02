package com.php25.common.db.core.sql;

import com.php25.common.db.exception.DbException;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/2/2 13:49
 */
public class GroupQuery extends BaseQuery0 {

    @Override
    public SqlParams select(Class<?> model, String... columns) {
        throw new DbException("不支持此操作");
    }

    @Override
    public SqlParams select(String... columns) {
        throw new DbException("不支持此操作");
    }

    @Override
    public SqlParams single() {
        throw new DbException("不支持此操作");
    }

    @Override
    public SqlParams select() {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams insert(M model) {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams insertIncludeNull(M model) {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams insertBatch(List<M> models) {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams update(M model) {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams updateIncludeNull(M model) {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams updateBatch(List<M> models) {
        throw new DbException("不支持此操作");
    }

    @Override
    public SqlParams delete() {
        throw new DbException("不支持此操作");
    }

    @Override
    public <M> SqlParams delete(M model) {
        throw new DbException("不支持此操作");
    }

    @Override
    public SqlParams count() {
        throw new DbException("不支持此操作");
    }
}
