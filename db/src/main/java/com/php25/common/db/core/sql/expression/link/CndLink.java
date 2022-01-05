package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.expression.Expression;

/**
 * @author penghuiping
 * @date 2022/1/1 22:09
 */
public class CndLink implements Link {
    private final Expression expression;

    CndLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
