package com.php25.common.coresample;

import com.google.common.collect.Lists;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/6 20:48
 * @Description:
 */
public class RxJavaTest {

    private Logger logger = LoggerFactory.getLogger(RxJavaTest.class);

    @Test
    public void test() {
        Flowable.fromCallable(() -> {
            System.out.println(Thread.currentThread().getName() + "===a");
            Thread.sleep(5000l);
            return Lists.newArrayList(1, 1, 1);
        }).subscribeOn(Schedulers.computation()).mergeWith(Flowable.fromCallable(() -> {
            System.out.println(Thread.currentThread().getName() + "===b");
            return Lists.newArrayList(2, 2, 2);
        }).subscribeOn(Schedulers.computation())).observeOn(Schedulers.single()).subscribe(integers -> {
            System.out.println(integers + ":" + Thread.currentThread().getName() + "===c");
        }, throwable -> {

        }, () -> {

            System.out.println("执行完成");
        });
//        System.out.println(Thread.currentThread().getName() + ":hello world");
//        Scanner scanner = new Scanner(System.in);
//        scanner.next();
    }
}
