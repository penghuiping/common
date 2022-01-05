package com.php25.common.db.core.sql;


import com.php25.common.db.core.sql.column.Column;

/**
 * sql查询条件 group、having、order、limit
 *
 * @author penghuiping
 * @date 2018-0-23
 */
public interface QueryOther {

    /**
     * having子句
     *
     * @param condition
     * @return 查询
     */
    Query having(String condition);

    /***
     * groupBy 子句
     * @param column
     * @return
     */
    Query groupBy(Column column);

    /***
     * orderBy 子句
     * 例如 orderBy id desc,user_id asc
     * @param orderBy
     * @return 查询
     */
    Query orderBy(String orderBy);

    /**
     * order by {column} asc
     *
     * @param column 列
     * @return 属性
     */
    Query asc(Column column);

    /**
     * order by {column} desc
     *
     * @param column 列
     * @return 查询
     */
    Query desc(Column column);

    /***
     * limit 子句
     * @param startRow 开始行数（包含）
     * @param pageSize 每页多少记录数
     * @return 差
     */
    Query limit(long startRow, long pageSize);


    /**
     * join 子句
     *
     * @param model 需要join的表实体类
     * @return 差
     */
    Query join(Class<?> model);

    /**
     * join 子句
     *
     * @param model 需要join的表实体类
     * @param alias as名
     * @return 差
     */
    Query join(Class<?> model, String alias);

    /**
     * left join 子句
     *
     * @param model 需要left join的表实体类
     * @return 差
     */
    Query leftJoin(Class<?> model);

    /**
     * left join 子句
     *
     * @param model 需要left join的表实体类
     * @param alias 别名
     * @return 查询
     */
    Query leftJoin(Class<?> model, String alias);

    /**
     * on 子句
     *
     * @param leftColumn  左列
     * @param rightColumn 右列
     * @return 查询
     */
    Query on(Column leftColumn, Column rightColumn);
}
