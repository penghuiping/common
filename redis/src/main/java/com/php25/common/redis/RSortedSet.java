package com.php25.common.redis;

/**
 * @author penghuiping
 * @date 2020/1/9 10:21
 */
public interface RSortedSet<T> {

    Boolean add(T t,double score);


}
