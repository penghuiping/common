package com.php25.common.timetasks.timewheel;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author penghuiping
 * @date 2020/5/15 13:31
 */
class WheelSlotList implements Iterable<TimeTask> {
    private TimeTask first;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private int size;


    /**
     * 往任务链表中加入任务
     *
     * @param task 任务
     */
    public void add(TimeTask task) {
        Lock wLock = readWriteLock.writeLock();
        wLock.lock();
        try {
            TimeTask ptr = this.first;
            if (ptr == null) {
                this.first = task;
                size++;
                return;
            }
            while (true) {
                if (ptr.next == null) {
                    ptr.next = task;
                    task.previous = ptr;
                    break;
                } else {
                    ptr = ptr.next;
                }
            }
            size++;
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 清空任务链表
     */
    public void clear() {
        Lock wLock = readWriteLock.writeLock();
        wLock.lock();
        try {
            this.first = null;
            this.size = 0;
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 获取任务链表大小
     *
     * @return 链表大小
     */
    public int getSize() {
        Lock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            return this.size;
        } finally {
            rLock.unlock();
        }
    }


    @Override
    public Iterator<TimeTask> iterator() {
        return new WheelSlotListIterator();
    }


    public class WheelSlotListIterator implements Iterator<TimeTask> {
        private TimeTask index = WheelSlotList.this.first;

        private TimeTask old;

        @Override
        public boolean hasNext() {
            Lock rLock = readWriteLock.readLock();
            rLock.lock();
            try {
                return index != null;
            } finally {
                rLock.unlock();
            }
        }

        @Override
        public TimeTask next() {
            Lock rLock = readWriteLock.readLock();
            rLock.lock();
            try {
                TimeTask result = index;
                old = result;
                index = index.next;
                return result;
            } finally {
                rLock.unlock();
            }
        }

        @Override
        public void remove() {
            Lock wLock = readWriteLock.writeLock();
            wLock.lock();
            try {
                TimeTask previous = old.previous;
                TimeTask next = old.next;
                if (previous != null && next != null) {
                    previous.next = next;
                    next.previous = previous;
                    size--;
                    return;
                }

                if (previous == null && next == null) {
                    WheelSlotList.this.first = null;
                    size--;
                    return;
                }

                if (previous == null) {
                    next.previous = null;
                    WheelSlotList.this.first = next;
                    size--;
                    return;
                }

                previous.next = null;
                size--;
            } finally {
                wLock.unlock();
            }
        }
    }


}
