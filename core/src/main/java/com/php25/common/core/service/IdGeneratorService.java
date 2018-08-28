package com.php25.common.core.service;

/**
 * @author penghuiping
 * @date 2017/9/18
 *
 * id生成器
 */
public interface IdGeneratorService {
    /**
     * 生成vip订单编号
     *
     * @return
     */
    public String getVipOrderNumber();


    /**
     * 实体类主键生产器 生成字符串主键
     *
     * @return string
     */
    public String getModelPrimaryKey();

    /**
     * 实体类主键生产器 生成整数主键
     *
     * @return
     */
    public Number getModelPrimaryKeyNumber();

}