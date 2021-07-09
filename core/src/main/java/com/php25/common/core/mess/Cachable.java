package com.php25.common.core.mess;

/**
 * @author penghuiping
 * @date 2021/7/9 09:08
 */
public interface Cachable {

    /**
     * 初始化缓存
     *
     * @return true: 初始化缓存成功
     */
    Boolean init();

    /**
     * 清楚缓存
     *
     * @return true: 清除缓存成功
     */
    Boolean clear();


    /**
     * 获取缓存名
     *
     * @return 缓存名
     */
    String name();


    /**
     * 打印缓存状态信息
     */
    void printStatusInfo();
}
