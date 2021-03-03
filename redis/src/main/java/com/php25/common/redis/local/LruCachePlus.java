package com.php25.common.redis.local;

import com.php25.common.core.mess.LruCache;

/**
 * @author penghuiping
 * @date 2021/3/2 10:14
 */
interface LruCachePlus extends LruCache<String, ExpiredCache> {
}
