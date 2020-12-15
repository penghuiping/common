package com.php25.common.db.core;

/**
 * 对应sql语句中的 group by
 *
 * @author penghuiping
 * @date 2018-08-23
 */
public class GroupBy {
    StringBuilder sb = new StringBuilder("GROUP BY ");
    boolean start = true;

    public void add(String col) {
        if (start) {
            sb.append(col).append(" ");
            start = false;
        } else {
            sb.append(",").append(col).append(" ");
        }

    }

    public String getGroupBy() {
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public GroupBy addHaving(String sql) {
        sb.append("HAVING ").append(sql).append(" ");
        return this;
    }


}
