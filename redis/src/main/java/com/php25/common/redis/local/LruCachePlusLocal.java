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
    private final Node<Map.Entry<String, ExpiredCache>> head;

    //newest
    private Node<Map.Entry<String, ExpiredCache>> tail;


    /**
     * 缓存最大数量
     */
    private final int maxEntry;

    public LruCachePlusLocal(int maxEntry) {
        this.maxEntry = maxEntry;
        this.head = new Node<>(new ImmutablePair<>("_virtual_node", null));
        this.tail = this.head;
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
        Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
        appendTail(node);
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

        Node<Map.Entry<String, ExpiredCache>> node = new Node<>(new ImmutablePair<>(key, value));
        appendTail(node);
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
            appendTail(ptr);
        }
        return expiredCache;
    }

    private void appendTail(Node<Map.Entry<String, ExpiredCache>> node) {
        node.previous = tail;
        node.next = null;
        tail.next = node;
        tail = node;
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
        ptr = ptr.next;
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
        ptr = ptr.next;
        while (true) {
            if (null == ptr) {
                break;
            }
            if (ptr.value.getKey().equals(key)) {
                ptr.previous.next = ptr.next;
                if (null == ptr.next) {
                    //尾节点情况
                    tail = ptr.previous;
                } else {
                    ptr.next.previous = ptr.previous;
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
        ptr = ptr.next;
        while (true) {
            if (null == ptr) {
                break;
            }
            sb.append(ptr.value.getKey()).append(",");
            ptr = ptr.next;
        }


        Node<Map.Entry<String, ExpiredCache>> ptr1 = tail;
        StringBuilder sb1 = new StringBuilder(" ");
        while (true) {
            if (ptr1 == head) {
                break;
            }
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
