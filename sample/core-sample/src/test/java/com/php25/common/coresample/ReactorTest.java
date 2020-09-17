package com.php25.common.coresample;

import com.php25.common.core.util.HttpClientUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.coresample.dto.Person;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: penghuiping
 * @date: 2019/3/14 21:54
 * @description:
 */

public class ReactorTest {
    private static final Logger log = LoggerFactory.getLogger(ReactorTest.class);

    @Test
    public void test() throws Exception {
        log.info("=========================>main thread:" + Thread.currentThread().getName());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Integer> flux = Flux.range(2, 7).doOnRequest(n -> log.info("Request {} number", n));
        flux.subscribeOn(Schedulers.parallel()).map(integer -> {
            log.info("=========================>map thread:" + Thread.currentThread().getName());
            Person person = new Person();
            person.setId(integer);
            person.setName(RandomUtil.getRandomLetters(4));
            return person;
        }).publishOn(Schedulers.elastic()).subscribe(person -> {
            log.info("=========================>subscribe thread:" + Thread.currentThread().getName());
            log.info(JsonUtil.toPrettyJson(person));
            countDownLatch.countDown();
        }, throwable -> {
        }, () -> {
        }, subscription -> {
            subscription.request(2);
        });
        countDownLatch.await();
    }

    @Test
    public void test1() throws Exception {
//        Flux.range(1, 6)
//                .doOnRequest(n -> log.info("Request {} number", n)) // 注意顺序造成的区别
////				.publishOn(Schedulers.elastic())
//                .doOnComplete(() -> log.info("Publisher COMPLETE 1"))
//                .map(i -> {
//                    log.info("Publish {}, {}", Thread.currentThread(), i);
//                    return 10 / (i - 3);
////					return i;
//                })
//                .doOnComplete(() -> log.info("Publisher COMPLETE 2"))
//				.subscribeOn(Schedulers.single())
////				.onErrorResume(e -> {
////					log.error("Exception {}", e.toString());
////					return Mono.just(-1);
////				})
////				.onErrorReturn(-1)
//                .subscribe(i -> log.info("Subscribe {}: {}", Thread.currentThread(), i),
//                        e -> log.error("error {}", e.toString()),
//                        () -> log.info("Subscriber COMPLETE")//,
////						s -> s.request(4)
//                );
//        Thread.sleep(2000);

        Mono.fromCallable(() -> {
            return Lists.newArrayList(1, 2, 3);
        }).flatMap(integers -> {
            log.info("==");
            return Mono.just(integers);
        }).publishOn(Schedulers.single()).subscribeOn(Schedulers.elastic()).subscribe(integers -> {
            log.info(integers.toString());
        });
        Thread.sleep(2000);
    }

    @Test
    public void testHttpAsync() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(60);
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 1; i++) {
            Mono.fromCallable(() -> {
                String resp = HttpClientUtil.httpGet(HttpClientUtil.getClient(), "http://www.baidu.com", null);
                return resp;
            }).subscribeOn(Schedulers.fromExecutor(executorService)).subscribe(s -> {
                Assert.assertTrue(s.contains("STATUS OK"));
            }, throwable -> {
                log.error("出错啦", throwable);
            }, () -> {
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.info("耗时:{}ms", System.currentTimeMillis() - startTime);
    }

    @Test
    public void testHttp() throws Exception {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            String resp = HttpClientUtil.httpGet(HttpClientUtil.getClient(), "http://www.baidu.com", null);
            Assert.assertTrue(resp.contains("STATUS OK"));
        }
        log.info("耗时:{}ms", System.currentTimeMillis() - startTime);
    }


}
