package com.php25.common.core.tree;


/**
 * @author penghuiping
 * @date 2020/7/3 14:51
 */
public interface VisitHandler<T, S> {

    void handle(T node, S context);
}
