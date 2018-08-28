package com.php25.common.core.service;

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
     * 批量软删除
     *
     * @param objs
     * @author penghuiping
     * @date 2016/11/25.
     */
    void softDelete(List<T> objs);
}
