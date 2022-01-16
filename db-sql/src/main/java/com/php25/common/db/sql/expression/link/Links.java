package com.php25.common.db.sql.expression.link;


import com.php25.common.db.sql.expression.Expression;

/**
 * @author penghuiping
 * @date 2021/12/28 21:32
 */
public abstract class Links {

    public static DefaultLink group(Expression expression) {
        return new DefaultLink().group(expression);
    }

    public static DefaultLink cnd(Expression expression) {
        return new DefaultLink().cnd(expression);
    }
}
