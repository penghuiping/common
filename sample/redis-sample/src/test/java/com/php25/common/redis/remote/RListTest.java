package com.php25.common.redis.remote;

import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/1/7 20:53
 */
public class RListTest {

    public void leftPushAndLeftPop(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.leftPush(1L);
        list.leftPush(2L);
        list.leftPush(3L);
        list.leftPush(4L);
        list.leftPush(5L);
        Assertions.assertThat(list.leftPop()).isEqualTo(5L);
        Assertions.assertThat(list.leftPop()).isEqualTo(4L);
        Assertions.assertThat(list.leftPop()).isEqualTo(3L);
        Assertions.assertThat(list.leftPop()).isEqualTo(2L);
        Assertions.assertThat(list.leftPop()).isEqualTo(1L);
    }

    public void rightPushAndRightPop(RedisManager redisManager) throws Exception {
        RList<String> list = redisManager.list("mylist1", String.class);
        list.rightPush("hello");
        list.rightPush("world");
        Assertions.assertThat(list.rightPop()).isEqualTo("world");
        Assertions.assertThat(list.rightPop()).isEqualTo("hello");
    }

    public void leftRange(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.leftPush(1L);
        list.leftPush(2L);
        list.leftPush(3L);
        list.leftPush(4L);
        list.leftPush(5L);

        List<Long> tmp = list.leftRange(2, 3);
        Assertions.assertThat(tmp.get(0)).isEqualTo(3);
        Assertions.assertThat(tmp.get(1)).isEqualTo(2);
    }

    public void leftTrim(RedisManager redisManager) throws Exception {
        RList<Long> list = redisManager.list("mylist", Long.class);
        list.leftPush(1L);
        list.leftPush(2L);
        list.leftPush(3L);
        list.leftPush(4L);
        list.leftPush(5L);

        list.leftTrim(0, 3);

        List<Long> result = list.leftRange(0, -1);
        Assertions.assertThat(result.get(0)).isEqualTo(5);
        Assertions.assertThat(result.get(1)).isEqualTo(4);
        Assertions.assertThat(result.get(2)).isEqualTo(3);
    }
}
