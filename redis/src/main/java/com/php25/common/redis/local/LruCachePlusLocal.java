package com.php25.common.redis.local;

import com.php25.common.core.exception.Exceptions;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/2/25 15:49
 */
class LruCachePlusLocal implements LruCachePlus {

    private final HashMap<String, ExpiredCache> map = new HashMap<>(16);

    //oldest
    private Node<Map.Entry<String, ExpiredCache>> head;

    //newest
    private Node<Map.Entry<String, ExpiredCache>> tail;

    /**
     * 缓存最大数量
     */
    private final int maxEntry;

    public LruCachePlusLocal(int maxEntry) {
        this.maxEntry = maxEntry;
    }


    @Override
    public void putValue(String key, ExpiredCache value) {
        if (!this.map.containsKey(key)) {
            if (this.size() >= maxEntry) {
                //移除最近访问最少的节点，且不是永久key的缓存
                Node<Map.Entry<String, ExpiredCache>> node = findOldestAndExpired();
                if (null != node) {
                    this.remove(node.value.getKey());
                } else {
                    throw Exceptions.throwIllegalStateException("Reach the maxEntry and no expired entry can be cleaned");
                }
            }
        } else {
            this.remove(key);
        }

        this.map.put(key, value);
        if (null == head) {
            Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
            node.previous = null;
            node.next = null;
            head = node;
            tail = node;
        } else {
            Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
            node.previous = tail;
            node.next = null;
            tail.next = node;
            tail = node;
        }
    }

    @Override
    public void putValueIfAbsent(String key, ExpiredCache value) {
        ExpiredCache previousCache = this.map.putIfAbsent(key, value);
        if (previousCache != null) {
            return;
        }

        if (this.size() >= maxEntry) {
            //移除最近访问最少的节点，且不是永久key的缓存
            Node<Map.Entry<String, ExpiredCache>> node = findOldestAndExpired();
            if (null != node) {
                this.remove(node.value.getKey());
            } else {
                throw Exceptions.throwIllegalStateException("Reach the maxEntry and no expired entry can be cleaned");
            }
        }

        if (null == head) {
            Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
            node.previous = null;
            node.next = null;
            head = node;
            tail = node;
        } else {
            Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
            node.previous = tail;
            node.next = null;
            tail.next = node;
            tail = node;
        }
    }

    @Override
    public ExpiredCache getValue(String key) {
        ExpiredCache expiredCache = this.map.get(key);
        if (null != expiredCache) {
            if (expiredCache.isExpired()) {
                //过期的缓存
                this.remove(key);
                return null;
            }
            Node<Map.Entry<String, ExpiredCache>> ptr = this.removeFromLinkNode(key);
            if (ptr == null) {
                throw Exceptions.throwImpossibleException();
            }
            tail.next = ptr;
            ptr.previous = tail;
            tail = ptr;
            tail.next = null;
        }
        return expiredCache;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean remove(String key) {
        this.map.remove(key);
        this.removeFromLinkNode(key);
        return true;
    }

    private Node<Map.Entry<String, ExpiredCache>> findOldestAndExpired() {
        Node<Map.Entry<String, ExpiredCache>> ptr = head;
        while (true) {
            if (null == ptr) {
                break;
            }
            if (ptr.value.getValue().getExpiredTime() != -1L) {
                break;
            }
            ptr = ptr.next;
        }
        return ptr;
    }

    private Node<Map.Entry<String, ExpiredCache>> removeFromLinkNode(String key) {
        Node<Map.Entry<String, ExpiredCache>> ptr = head;
        while (true) {
            if (null == ptr) {
                break;
            }
            if (ptr.value.getKey().equals(key)) {
                Node<Map.Entry<String, ExpiredCache>> previous = ptr.previous;
                if (null == previous) {
                    head = ptr.next;
                    head.previous = null;
                } else {
                    if (null == ptr.next) {
                        tail = ptr.previous;
                        tail.next = null;
                    } else {
                        previous.next = ptr.next;
                        ptr.next.previous = previous;
                    }
                }
                break;
            }
            ptr = ptr.next;
        }
        if (null != ptr) {
            ptr.previous = null;
            ptr.next = null;
        }
        return ptr;
    }

    @Override
    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public String toString() {
        Node<Map.Entry<String, ExpiredCache>> ptr = head;
        StringBuilder sb = new StringBuilder(" ");
        while (ptr != null) {
            sb.append(ptr.value.getKey()).append(",");
            ptr = ptr.next;
        }


        Node<Map.Entry<String, ExpiredCache>> ptr1 = tail;
        StringBuilder sb1 = new StringBuilder(" ");
        while (ptr1 != null) {
            sb1.append(ptr1.value.getKey()).append(",");
            ptr1 = ptr1.previous;
        }
        return sb.substring(0, sb.length() - 1) + "\n" + sb1.substring(0, sb1.length() - 1);
    }

    @Override
    public Iterator<Map.Entry<String, ExpiredCache>> getIterator() {
        return this.map.entrySet().iterator();
    }

    static class Node<T> {
        Node<T> previous;
        Node<T> next;
        T value;

        public Node(T value) {
            this.value = value;
        }
    }
}
