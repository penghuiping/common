package com.php25.common.db.cnd;

/**
 * 对应sql语句中的 order by
 *
 * @author penghuiping
 * @date 2018-08-23
 */
public class OrderBy {
    StringBuilder sb = new StringBuilder("ORDER BY ");

    public void add(String orderBy) {
        sb.append(orderBy).append(" ,");
    }

    public String getOrderBy() {
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
