package com.php25.common.coresample;

import com.php25.common.core.mess.LruCache;
import com.php25.common.core.mess.LruCacheImpl;
import org.junit.Test;

/**
 * @author penghuiping
 * @date 2020/8/25 14:28
 */
public class LruCacheTest {

    @Test
    public void test() {
        LruCache<String,String> lruCache = new LruCacheImpl<>(6);
        lruCache.putValue("1","a");
        lruCache.putValue("2","b");
        lruCache.putValue("3","c");
        lruCache.putValue("4","d");
        lruCache.putValue("5","e");
        lruCache.putValue("6","f");
        lruCache.getValue("1");
        lruCache.putValue("7","g");
        System.out.println();

    }
}
