package com.php25.common.coresample;

import com.php25.common.core.mess.LruCache;
import com.php25.common.core.mess.LruCacheImpl;
import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(lruCache.size()).isEqualTo(6);
        lruCache.putValue("7","g");
        lruCache.putValue("8","h");
        lruCache.putValue("9","i");
        Assertions.assertThat(lruCache.size()).isEqualTo(6);
        Assertions.assertThat(lruCache.getValue("1")).isEqualTo(null);
        Assertions.assertThat(lruCache.getValue("2")).isEqualTo(null);
        Assertions.assertThat(lruCache.getValue("4")).isEqualTo("d");
    }


}
