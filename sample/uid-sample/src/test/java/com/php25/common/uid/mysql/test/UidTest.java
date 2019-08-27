package com.php25.common.uid.mysql.test;

import com.baidu.fsg.uid.UidGenerator;
import com.php25.common.uid.mysql.CommonAutoConfigure;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
public class UidTest {

    private static final Logger log = LoggerFactory.getLogger(UidTest.class);

    @Autowired
    private UidGenerator uidGenerator;

    @Test
    public void test() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono.fromCallable(() -> {
            long uid = uidGenerator.getUID();
            String uidStr = uidGenerator.parseUID(uid);
            return uidStr;
        }).publishOn(Schedulers.parallel()).repeat(1000-1).collectList().subscribeOn(Schedulers.parallel()).subscribe(s -> {
            Set set = Sets.newHashSet(s);
            Assertions.assertThat(set.size()).isEqualTo(1000);
            log.info("集合大小:{}",set.size());
        }, throwable -> {

        }, () -> {
            countDownLatch.countDown();
        }, subscription -> {
            subscription.request(1000);
        });

        countDownLatch.await();


    }

}
