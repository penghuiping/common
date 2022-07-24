package com.php25.common.core.mess.list;

/**
 * @author penghuiping
 * @date 2022/7/23 09:23
 */
public class ListNode<T> {
    private T value;
    private ListNode<T> next;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ListNode<T> getNext() {
        return next;
    }

    public void setNext(ListNode<T> next) {
        this.next = next;
    }
}
