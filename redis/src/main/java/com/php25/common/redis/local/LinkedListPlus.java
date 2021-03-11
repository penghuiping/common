package com.php25.common.redis.local;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author penghuiping
 * @date 2021/3/5 13:52
 */
public class LinkedListPlus<T> extends LinkedList<T> {

    private final BlockingQueue<Boolean> pipe = new LinkedBlockingQueue<>();

    public LinkedListPlus() {
    }

    public LinkedListPlus(@NotNull Collection<? extends T> c) {
        super(c);
    }

    public BlockingQueue<Boolean> getPipe() {
        return pipe;
    }
}
