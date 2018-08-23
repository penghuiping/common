package com.php25.common.core.service;

/**
 * @author: penghuiping
 * @date: 2018/5/30 09:55
 *
 * 一致性hash
 */
public interface ConsistentHashingService {

    /**
     * 更具关键字key,得到应当路由到的结点
     *
     * @param key
     * @return
     */
    String getServer(String key);
}
