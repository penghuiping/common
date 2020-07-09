package com.php25.common.core.tree;


/**
 * @author penghuiping
 * @date 2020/7/1 17:05
 */
public interface TreeAble<T> {

    /**
     * 用于获取parentId
     *
     * @return
     */
    T getParentId();

    /**
     * 用于获取id
     *
     * @return
     */
    T getId();

}
