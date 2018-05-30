package com.php25.common.service;

/**
 * @Auther: penghuiping
 * @Date: 2018/5/30 09:55
 * @Description:
 */
public interface ConsistentHashingService {

    /**
     * 更具关键字key,得到应当路由到的结点
     *
     * @param key
     * @return
     */
    public String getServer(String key);
}
