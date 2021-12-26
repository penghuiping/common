package com.php25.common.db.core.sql;


import java.util.List;

/**
 * 查询
 *
 * @author penghuiping
 * @date 2020/12/1 16:51
 */
public interface Query extends QueryConditionAnd, QueryConditionOr, QueryConditionWhere, QueryAction, QueryOther {
    /***
     * 获取参数
     * @return 查询参数
     */
    List<Object> getParams();

    /**
     * 获取sql
     *
     * @return sql
     */
    StringBuilder getSql();

    /**
     * 新增一条记录
     *
     * @param model      需要新增的实体类
     * @param ignoreNull 是否忽略实体对象中为null的属性项,true:忽略,false:不忽略
     * @return 返回sql语句
     */
    <M> SqlParams insert(M model, boolean ignoreNull);


    /**
     * 增加分页，排序
     */
    void addAdditionalPartSql();
}
