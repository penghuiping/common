package com.php25.common.core.mess;

/**
 * @author penghuiping
 * @date 2017/9/18
 * <p>
 * id生成器
 */
public interface IdGenerator {

    /**
     * java自带的生成uuid
     *
     * @return
     */
    public String getUUID();

    /**
     * 根据时间与网卡生成uuid
     *
     * @return
     */
    public String getJUID();


    /**
     * 获取雪花算法id
     *
     * @return
     */
    public Long getSnowflakeId();

}
