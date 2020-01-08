package com.php25.common.redissample;

import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/7 20:53
 */
public class RListTest {

    public void lpush_lpop(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.lpush(1L);
        list.lpush(2L);
        list.lpush(3L);
        list.lpush(4L);
        list.lpush(5L);
        Assertions.assertThat(list.lpop()).isEqualTo(5L);
        Assertions.assertThat(list.lpop()).isEqualTo(4L);
        Assertions.assertThat(list.lpop()).isEqualTo(3L);
        Assertions.assertThat(list.lpop()).isEqualTo(2L);
        Assertions.assertThat(list.lpop()).isEqualTo(1L);
    }

    public void rpush_rpop(RedisManager redisManager) throws Exception {
        RList<String> list = redisManager.list("mylist1", String.class);
        list.rpush("hello");
        list.rpush("world");
        Assertions.assertThat(list.rpop()).isEqualTo("world");
        Assertions.assertThat(list.rpop()).isEqualTo("hello");
    }

    public void lrange(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.lpush(1L);
        list.lpush(2L);
        list.lpush(3L);
        list.lpush(4L);
        list.lpush(5L);

        List<Long> tmp = list.lrange(2, 3);
        Assertions.assertThat(tmp.get(0)).isEqualTo(3);
        Assertions.assertThat(tmp.get(1)).isEqualTo(2);
    }

    public void ltrim(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.lpush(1L);
        list.lpush(2L);
        list.lpush(3L);
        list.lpush(4L);
        list.lpush(5L);

        list.ltrim(0,3);

        List<Long> result = list.lrange(0,-1);
        Assertions.assertThat(result.get(0)).isEqualTo(5);
        Assertions.assertThat(result.get(1)).isEqualTo(4);
        Assertions.assertThat(result.get(2)).isEqualTo(3);
    }
}
