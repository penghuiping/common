package com.php25.common.redis.local;

import com.php25.common.core.mess.LruCache;

import java.util.Iterator;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/3/2 10:14
 */
interface LruCachePlus extends LruCache<String, ExpiredCache> {

    /**
     * 获取迭代器
     *
     * @return 迭代器
     */
    Iterator<Map.Entry<String, ExpiredCache>> getIterator();
}
