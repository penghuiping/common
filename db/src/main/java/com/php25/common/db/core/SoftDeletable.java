package com.php25.common.db.core;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 软删除
 *
 * @author penghuiping
 * @date 2016/11/25.
 */
public interface SoftDeletable<T> {

    /**
     * 软删除
     *
     * @param obj
     * @author penghuiping
     * @date 2016/11/25.
     */
    void softDelete(T obj);

    /**
     * 软删除-异步调用
     *
     * @param obj
     * @author penghuiping
     * @date 2019/3/19.
     */
    Mono<Boolean> softDeleteAsync(T obj);

    /**
     * 批量软删除
     *
     * @param objs
     * @author penghuiping
     * @date 2016/11/25.
     */
    void softDelete(List<T> objs);

    /**
     * 批量软删除-异步调用
     *
     * @param objs
     * @author penghuiping
     * @date 2019/3/19.
     */
    Mono<Boolean> softDeleteAsync(List<T> objs);
}
