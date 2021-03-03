package com.php25.common.redis.local;

import com.php25.common.redis.RSortedSet;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalSortedSet<T> implements RSortedSet<T> {

    private TreeSet<T> set;

    @Override
    public Boolean add(T t, double score) {
        return true;
    }

    @Override
    public Set<T> range(long start, long end) {
        return null;
    }

    @Override
    public Set<T> reverseRange(long start, long end) {
        return null;
    }

    @Override
    public Set<T> rangeByScore(double min, double max) {
        return null;
    }

    @Override
    public Set<T> reverseRangeByScore(double min, double max) {
        return null;
    }

    @Override
    public Long rank(T t) {
        NavigableSet<T> navigableSet = set.headSet(t, true);
        return (long) navigableSet.size();
    }

    @Override
    public Long reverseRank(T t) {
        NavigableSet<T> navigableSet = set.descendingSet().headSet(t, true);
        return (long) navigableSet.size();
    }

    @Override
    public Long removeRangeByScore(double min, double max) {
        return null;
    }
}
