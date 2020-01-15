package com.php25.common.db.cnd;


import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2018-08-23
 **/
public interface QueryExecute {

    /**
     * 查询，并映射到指定类上
     *
     * @param columns
     * @param resultType
     * @return
     */
    public <T> List<T> select(Class resultType, String... columns);

    /***
     * 指定字段查询
     * @param columns
     * @return 查询结果
     */
    <M> List<M> select(String... columns);


    /**
     * 查询结果集，结果集每一条并映射到map
     *
     * @return
     */
    List<Map> mapSelect();


    /**
     * 查询结果集，结果集每一条并映射到map
     *
     * @param columns
     * @return
     */
    List<Map> mapSelect(String... columns);

    /**
     * 查询一条记录，映射到Map
     *
     * @return
     */
    Map mapSingle();

    /**
     * 查询出一条，如果没有，返回null
     *
     * @return
     */
    <M> M single();


    /**
     * 查询所有字段
     *
     * @return 查询结果
     */
    <M> List<M> select();

    /***
     * 更新，不包括null值
     * @param t,任意对象，或者Map
     * @return 影响的行数
     */
    <M> int update(M t);

    /***
     * 更新，包括null值
     * @param t 任意对象或者Map
     * @return 影响的行数
     */
    <M> int updateIncludeNull(M t);

    /**
     * 批量更新操作
     *
     * @param lists
     * @param <T>
     * @return
     */
    public <T> int[] updateBatch(List<T> lists);


    /***
     * 插入，不包括null值
     * @param m
     * @return 影响的行数
     */
    <M> int insert(M m);


    /***
     * 插入，包括null值
     * @param m
     * @return 影响的行数
     */
    <M> int insertIncludeNull(M m);

    /**
     * 批量插入操作
     *
     * @param list
     * @param <M>
     * @return
     */
    <M> int[] insertBatch(List<M> list);


    /***
     * 删除
     * @return 影响的行数
     */
    int delete();

    /***
     * count
     * @return 总行数
     */
    long count();
}
