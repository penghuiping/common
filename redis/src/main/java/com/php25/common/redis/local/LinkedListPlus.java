package com.php25.common.redis.local;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author penghuiping
 * @date 2021/3/5 13:52
 */
public class LinkedListPlus<T> extends LinkedList<T> {

    private Lock lock;

    private Condition notEmpty;

    public LinkedListPlus() {
    }

    public LinkedListPlus(@NotNull Collection<? extends T> c) {
        super(c);
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Condition getNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(Condition notEmpty) {
        this.notEmpty = notEmpty;
    }
}
