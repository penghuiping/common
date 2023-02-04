package com.php25.common.redis.local;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.redis.Person;
import com.php25.common.redis.RSet;
import com.php25.common.redis.RedisManager;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * @author penghuiping
 * @date 2021/3/2 17:37
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RSetLocalTest {
    private static final Logger log = LoggerFactory.getLogger(RSetLocalTest.class);
    private RSet<Person> rSet;
    private RedisManager redisManager;

    @Before
    public void before() throws Exception {
        this.redisManager = new LocalRedisManager(1024);
        this.rSet = redisManager.set("my_set", Person.class);
        this.rSet.add(new Person(12, "jack"));
        this.rSet.add(new Person(13, "mary"));
    }

    @Test
    public void add() throws Exception {
        this.rSet.add(new Person(11, "tom"));
        Assertions.assertThat(this.rSet.size()).isEqualTo(3);
    }

    @Test
    public void remove() throws Exception {
        Assertions.assertThat(this.rSet.size()).isEqualTo(2);
        Person mary = new Person(13, "mary");
        this.rSet.remove(mary);
        Assertions.assertThat(this.rSet.size()).isEqualTo(1);
    }

    @Test
    public void members() throws Exception {
        Person tom = new Person(11, "tom");
        this.rSet.add(tom);
        Set<Person> set = this.rSet.members();
        log.info(JsonUtil.toJson(set));
        Assertions.assertThat(set.size()).isEqualTo(3);
        Assertions.assertThat(set.contains(tom)).isEqualTo(true);
    }

    @Test
    public void isMember() throws Exception {
        Person jack = new Person(12, "jack");
        Assertions.assertThat(this.rSet.isMember(jack)).isEqualTo(true);
    }

    @Test
    public void pop() throws Exception {
        Person person = this.rSet.pop();
        log.info("pop出的人为:{}", JsonUtil.toJson(person));
        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(this.rSet.size()).isEqualTo(1);
    }

    @Test
    public void union() throws Exception {
        RSet<Person> otherSet = this.redisManager.set("other_set", Person.class);
        otherSet.add(new Person(20, "tom"));
        otherSet.add(new Person(22, "alice"));
        Set<Person> unionSet = this.rSet.union("other_set");
        log.info("union_set:{}", JsonUtil.toJson(unionSet));
        Assertions.assertThat(unionSet.size()).isEqualTo(4);
    }

    @Test
    public void inter() throws Exception {
        RSet<Person> otherSet = this.redisManager.set("other_set", Person.class);
        Person jack = new Person(12, "jack");
        Person alice = new Person(22, "alice");
        otherSet.add(jack);
        otherSet.add(alice);
        Set<Person> interSet = this.rSet.inter("other_set");
        log.info("inter_set:{}", JsonUtil.toJson(interSet));
        Assertions.assertThat(interSet.size()).isEqualTo(1);
        Assertions.assertThat(interSet.contains(jack)).isTrue();
    }

    @Test
    public void diff() throws Exception {
        RSet<Person> otherSet = this.redisManager.set("other_set", Person.class);
        Person jack = new Person(12, "jack");
        Person alice = new Person(22, "alice");
        Person mary = new Person(13, "mary");
        otherSet.add(jack);
        otherSet.add(alice);
        Set<Person> diffSet = this.rSet.diff("other_set");
        log.info("diff_set:{}", JsonUtil.toJson(diffSet));
        Assertions.assertThat(diffSet.size()).isEqualTo(1);
        Assertions.assertThat(diffSet.contains(mary)).isTrue();

    }

    @Test
    public void size() throws Exception {
        Assertions.assertThat(this.rSet.size()).isEqualTo(2);
    }

    @Test
    public void getRandomMember() throws Exception {
        Person person = this.rSet.getRandomMember();
        log.info("getRandomMember出的人为:{}", JsonUtil.toJson(person));
        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(this.rSet.size()).isEqualTo(2);
    }

    @Test
    public void getRandomNumber() throws Exception {
        Set<String> numbers = RandomUtil.getAllPossibleDifferentNumbers(6);
        RSet<String> numbers0 = this.redisManager.set("random_numbers", String.class);
        numbers0.add(numbers);
        for (int i = 0; i < 10; i++) {
            log.info("随机数:{}", numbers0.pop());
        }
    }
}
