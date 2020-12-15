package com.php25.common.db.core.sql;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/1 16:39
 */
public interface QuerySql {

    /**
     * 查询，并映射到指定类上，并指定需要映射的字段
     *
     * @param model   需要映射的model类
     * @param columns 需要映射的字段
     * @return 返回对应sql语句
     */
    SqlParams select(Class<?> model, String... columns);

    /***
     * 查询，并指定需要映射的字段
     * @param columns 需要映射的字段
     * @return 返回对应sql语句
     */
    SqlParams select(String... columns);

    /**
     * 查询唯一记录
     *
     * @return 返回对应sql语句
     */
    SqlParams single();

    /**
     * 查询,并映射所有字段
     *
     * @return 返回对应sql语句
     */
    SqlParams select();

    /***
     * 新增一条记录,并且把model属性中为null值的排除
     * @param model 需要新增的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams insert(M model);

    /***
     * 新增一条记录,并包括model中属性为null值的字段
     * @param model 需要新增的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams insertIncludeNull(M model);

    /**
     * 批量新增记录
     *
     * @param models 需要新增的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams insertBatch(List<M> models);

    /***
     * 更新一条记录, 并且把model属性中为null值的排除
     * @param model 需要更新的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams update(M model);

    /***
     * 更新一条记录, 并包括model中属性为null值的字段
     * @param model 需要更新的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams updateIncludeNull(M model);

    /**
     * 批量更新操作
     *
     * @param models 需要更新的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams updateBatch(List<M> models);

    /***
     * 删除
     * @return 返回对应sql语句
     */
    SqlParams delete();


    /***
     * 删除
     * @param model 需要删除的实体类
     * @return 返回对应sql语句
     */
    <M> SqlParams delete(M model);


    /***
     * 统计总数
     *
     * @return 返回对应sql语句
     */
    SqlParams count();
}
