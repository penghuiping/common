package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.core.sql.expression.Expression;

/**
 * @author penghuiping
 * @date 2021/12/28 21:33
 */
public class AndLink implements Link {
    private final Expression expression;

    AndLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format(" %s %s", DbConstant.AND, expression.toString());
    }
}
