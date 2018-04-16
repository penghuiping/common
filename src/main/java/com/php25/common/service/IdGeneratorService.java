package com.php25.common.service;

/**
 * Created by penghuiping on 2017/9/18.
 */
public interface IdGeneratorService {

    /**
     * 生成商品订单编号
     * @return
     */
    public String getGoodsOrderNumber();

    /**
     * 生成vip订单编号
     * @return
     */
    public String getVipOrderNumber();

    /**
     * 生成体检卡与体验券编号
     * @return
     */
    public String getTicketsNumber();

    /**
     * 生成活动订单编号
     * @return
     */
    public String getActivityOrderNumber();


    /**
     * 实体类主键生产器
     * @return
     */
    public String getModelPrimaryKey();

}
