package com.php25.common.core.mess.list;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 线性链表
 *
 * @author penghuiping
 * @date 2022/7/23 09:37
 */
public class LinkedList<T> implements Iterable<T> {
    private final ListNode<T> head;

    public LinkedList() {
        head = new ListNode<>();
    }


    public boolean add(long index, T value) {
        int i = 0;
        ListNode<T> current = head;
        while (current != null) {
            if (i == index) {
                ListNode<T> next = current.getNext();
                ListNode<T> newNode = new ListNode<>();
                newNode.setNext(next);
                newNode.setValue(value);
                current.setNext(newNode);
                break;
            }
            current = current.getNext();
            i++;
        }
        return true;
    }

    public boolean remove(long index, T value) {
        return true;
    }

    public long size() {
        int i = 0;
        ListNode<T> next = head.getNext();
        while (next != null) {
            next = next.getNext();
            i++;
        }
        return i;
    }

    /**
     * 把链表的[m,n]中的数据进行翻转
     *
     * @param m int整型
     * @param n int整型
     */
    public void reverseBetween(int m, int n) {
        long size = this.size();

        if (n <= m || n > size || m > size) {
            return;
        }

        ListNode<T> leftOuter = null;
        ListNode<T> rightOuter = null;
        ListNode<T> left = null;
        ListNode<T> right = null;

        ListNode<T> previous = head;
        ListNode<T> current = head.getNext();
        int index = 0;
        while (true) {
            if (current == null) {
                break;
            }
            if (index == m) {
                left = current;
                leftOuter = previous;
            }

            if (index == n) {
                right = current;
                rightOuter = current.getNext();
            }

            previous = current;
            current = current.getNext();
            index++;
        }

        this.reverseBetween2Node(left, right);
        leftOuter.setNext(right);
        left.setNext(rightOuter);
    }

    private void reverseBetween2Node(ListNode<T> left, ListNode<T> right) {
        ListNode<T> previous = head;
        ListNode<T> current = head;

        boolean startReverse = false;

        while (null != current) {
            if (previous == left) {
                startReverse = true;
            }

            if (startReverse) {
                if (current == right) {
                    startReverse = false;
                }
                ListNode<T> next = current.getNext();
                current.setNext(previous);
                previous = current;
                current = next;
                continue;
            }

            previous = current;
            current = current.getNext();
        }
        left.setNext(null);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        java.util.Iterator<T> iterator = iterator();
        sb.append("[");
        while (iterator.hasNext()) {
            T next = iterator.next();
            sb.append(next);
            sb.append(",");
        }
        sb.append("]");
        return sb.toString().replace(",]", "]");
    }

    @NotNull
    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator<>(this);
    }


    private static class Iterator<T> implements java.util.Iterator<T> {
        private ListNode<T> previous;
        private ListNode<T> current;

        public Iterator(LinkedList<T> list) {
            this.previous = list.head;
            this.current = list.head;
        }

        @Override
        public boolean hasNext() {
            return current.getNext() != null;
        }

        @Override
        public T next() {
            ListNode<T> next = current.getNext();
            previous = current;
            current = next;
            return next.getValue();
        }

        @Override
        public void remove() {
            ListNode<T> next = current.getNext();
            previous.setNext(next);
            current = next;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            while (hasNext()) {
                T next = next();
                action.accept(next);
            }
        }
    }
}
