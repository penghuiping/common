package com.php25.common.id;

import com.google.common.collect.Lists;
import com.php25.common.id.service.CachedIdGenerator;
import com.php25.common.id.service.IdGenerator;
import com.php25.common.id.service.SegmentIdService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author penghuiping
 * @date 2022/1/7 20:51
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class IdTest {

    private final static Logger log = LoggerFactory.getLogger(IdTest.class);

    @ClassRule
    public static GenericContainer mysql = new GenericContainer<>("mysql:5.7").withExposedPorts(3306);

    static {
        mysql.setPortBindings(Lists.newArrayList("3306:3306"));
        mysql.withEnv("MYSQL_USER", "root");
        mysql.withEnv("MYSQL_ROOT_PASSWORD", "root");
        mysql.withEnv("MYSQL_DATABASE", "test");
    }

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    JdbcTemplate jdbcTemplate;

    IdGenerator idGenerator;

    @Autowired
    SegmentIdService segmentIdService;

    @Before
    public void before() {
        String ddl = "CREATE TABLE `t_id_info` (\n" +
                "                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',\n" +
                "                                `biz_type` varchar(63) NOT NULL DEFAULT '' COMMENT '业务类型，唯一',\n" +
                "                                `begin_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始id，仅记录初始值，无其他含义。初始化时begin_id和max_id应相同',\n" +
                "                                `max_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '当前最大id',\n" +
                "                                `step` int(11) DEFAULT '0' COMMENT '步长',\n" +
                "                                `delta` int(11) NOT NULL DEFAULT '1' COMMENT '每次id增量',\n" +
                "                                `remainder` int(11) NOT NULL DEFAULT '0' COMMENT '余数',\n" +
                "                                `create_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '创建时间',\n" +
                "                                `update_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '更新时间',\n" +
                "                                `version` bigint(20) NOT NULL DEFAULT '0' COMMENT '版本号',\n" +
                "                                PRIMARY KEY (`id`),\n" +
                "                                UNIQUE KEY `uniq_biz_type` (`biz_type`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT 'id信息表';\n";
        jdbcTemplate.execute(ddl);

        String inert = "INSERT INTO `t_id_info` (`id`, `biz_type`, `begin_id`, `max_id`, `step`, `delta`, `remainder`, `create_time`, `update_time`, `version`)\n" +
                "VALUES\n" +
                "    (1, 'test', 1, 1, 100000, 1, 0, '2018-07-21 23:52:58', '2018-07-22 23:19:27', 1);";
        jdbcTemplate.execute(inert);

        this.idGenerator = new CachedIdGenerator(Executors.newFixedThreadPool(2), "test", segmentIdService);
    }

    @Test
    public void test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        Future<Set<Long>> future0 = executorService.submit(() -> {
            Set<Long> set = new HashSet<>(2000);
            for (int j = 0; j < 2000; j++) {
                Long id = this.idGenerator.nextId();
                set.add(id);
            }
            return set;
        });
        Future<Set<Long>> future1 = executorService.submit(() -> {
            Set<Long> set = new HashSet<>(2000);
            for (int j = 0; j < 2000; j++) {
                Long id = this.idGenerator.nextId();
                set.add(id);
            }
            return set;

        });
        Future<Set<Long>> future2 = executorService.submit(() -> {
            Set<Long> set = new HashSet<>(2000);
            for (int j = 0; j < 2000; j++) {
                Long id = this.idGenerator.nextId();
                set.add(id);
            }
            return set;
        });

        Set<Long> set0 = future0.get();
        Set<Long> set1 = future1.get();
        Set<Long> set2 = future2.get();

        Assertions.assertThat(set0.size()).isEqualTo(2000);
        Assertions.assertThat(set1.size()).isEqualTo(2000);
        Assertions.assertThat(set2.size()).isEqualTo(2000);


        Assertions.assertThat(set0.containsAll(set1)).isEqualTo(false);
        Assertions.assertThat(set0.containsAll(set2)).isEqualTo(false);
        Assertions.assertThat(set1.containsAll(set2)).isEqualTo(false);

        set0.addAll(set1);
        set0.addAll(set2);
        Assertions.assertThat(set0.size()).isEqualTo(6000);
    }
}
