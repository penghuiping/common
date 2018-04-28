package com.php25.common.service;

/**
 * Created by penghuiping on 2017/9/18.
 */
public interface IdGeneratorService {
    /**
     * 生成vip订单编号
     * @return
     */
    public String getVipOrderNumber();


    /**
     * 实体类主键生产器
     * @return
     */
    public String getModelPrimaryKey();

}
