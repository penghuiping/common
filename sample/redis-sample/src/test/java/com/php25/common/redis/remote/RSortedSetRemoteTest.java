package com.php25.common.redis.remote;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.Person;
import com.php25.common.redis.RSortedSet;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.impl.RedisManagerImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author penghuiping
 * @date 2021/3/14 21:19
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RSortedSetRemoteTest {

    private static final Logger log = LoggerFactory.getLogger(RSortedSetRemoteTest.class);

    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);
    private RedisManager redisManager;
    private RSortedSet<Person> rSortedSet;

    @Before
    public void before() {
        String address = redis.getContainerIpAddress();
        Integer port = redis.getFirstMappedPort();
        //单机
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setDatabase(0);
        redisConfiguration.setHostName(address);
        redisConfiguration.setPort(port);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        this.redisManager = new RedisManagerImpl(new StringRedisTemplate(lettuceConnectionFactory));

        this.rSortedSet = redisManager.zset("my_sorted_set", Person.class);
        this.rSortedSet.add(new Person(11, "tom"), 11);
        this.rSortedSet.add(new Person(12, "jack"), 12);
        this.rSortedSet.add(new Person(12, "ted"), 12);
        this.rSortedSet.add(new Person(13, "mary"), 13);
    }

    @Test
    public void add() {
        this.rSortedSet.add(new Person(11, "alice"), 11);
        Assertions.assertThat(this.rSortedSet.size()).isEqualTo(5);
    }

    @Test
    public void range() {
        Set<Person> persons = this.rSortedSet.range(0, 1);
        Person tom = new Person(11, "tom");
        Person jack = new Person(12, "jack");

        List<Person> personList = new ArrayList<>(persons);
        Assertions.assertThat(personList.get(0)).isEqualTo(tom);
        Assertions.assertThat(personList.get(1)).isEqualTo(jack);
        Assertions.assertThat(persons.size()).isEqualTo(2);
    }

    @Test
    public void reverseRange() {
        Set<Person> persons = this.rSortedSet.reverseRange(0, 1);
        Person ted = new Person(12, "ted");
        Person mary = new Person(13, "mary");

        List<Person> personList = new ArrayList<>(persons);
        Assertions.assertThat(personList.get(0)).isEqualTo(mary);
        Assertions.assertThat(personList.get(1)).isEqualTo(ted);
        Assertions.assertThat(persons.size()).isEqualTo(2);
    }

    @Test
    public void rangeByScore() {
        Set<Person> persons = this.rSortedSet.rangeByScore(11, 12);

        Person tom = new Person(11, "tom");
        Person jack = new Person(12, "jack");
        Person ted = new Person(12, "ted");
        List<Person> personList = new ArrayList<>(persons);
        Assertions.assertThat(personList.get(0)).isEqualTo(tom);
        Assertions.assertThat(personList.get(1)).isEqualTo(jack);
        Assertions.assertThat(personList.get(2)).isEqualTo(ted);
        Assertions.assertThat(persons.size()).isEqualTo(3);
    }

    @Test
    public void reverseRangeByScore() {
        Set<Person> persons = this.rSortedSet.reverseRangeByScore(11, 12);

        Person tom = new Person(11, "tom");
        Person jack = new Person(12, "jack");
        Person ted = new Person(12, "ted");
        List<Person> personList = new ArrayList<>(persons);
        Assertions.assertThat(personList.get(0)).isEqualTo(ted);
        Assertions.assertThat(personList.get(1)).isEqualTo(jack);
        Assertions.assertThat(personList.get(2)).isEqualTo(tom);
        Assertions.assertThat(persons.size()).isEqualTo(3);
    }

    @Test
    public void rank() {
        Person tom = new Person(11, "tom");
        Person jack = new Person(12, "jack");
        Long index0 = this.rSortedSet.rank(tom);
        Assertions.assertThat(index0).isEqualTo(0);
        Long index1 = this.rSortedSet.rank(jack);
        Assertions.assertThat(index1).isEqualTo(1);
    }

    @Test
    public void reverseRank() {
        Person tom = new Person(11, "tom");
        Person jack = new Person(12, "jack");
        Long index0 = this.rSortedSet.reverseRank(tom);
        Assertions.assertThat(index0).isEqualTo(3);
        Long index1 = this.rSortedSet.reverseRank(jack);
        Assertions.assertThat(index1).isEqualTo(2);
    }

    @Test
    public void removeRangeByScore() {
        this.rSortedSet.removeRangeByScore(11, 12);
        Person mary = new Person(13, "mary");
        Set<Person> persons = this.rSortedSet.range(0, this.rSortedSet.size());
        Assertions.assertThat(persons.size()).isEqualTo(1);
        Assertions.assertThat(this.rSortedSet.size()).isEqualTo(1);
        Assertions.assertThat(persons.contains(mary)).isTrue();
    }

}
