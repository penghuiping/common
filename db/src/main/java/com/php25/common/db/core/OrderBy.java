package com.php25.common.db.core;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应sql语句中的 order by
 *
 * @author penghuiping
 * @date 2018-08-23
 */
public class OrderBy {
    StringBuilder sb = new StringBuilder("ORDER BY ");

    private final List<Pair<String, String>> orders = new ArrayList<>();


    public void add(String orderBy) {
        String[] pairs = orderBy.trim().split(" ");
        String left = pairs[0].trim();
        if (left.contains(".")) {
            left = left.split("\\.")[1];
        }
        String right = pairs[1].trim();
        orders.add(new ImmutablePair<>(left, right));
        sb.append(orderBy).append(" ,");
    }

    public String getOrderBy() {
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public List<Pair<String, String>> getOrders() {
        return orders;
    }
}
