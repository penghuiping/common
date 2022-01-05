package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.core.sql.expression.Expression;

/**
 * @author penghuiping
 * @date 2021/12/28 21:34
 */
public class OrLink implements Link {
    private final Expression expression;

    OrLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format(" %s %s", DbConstant.OR, expression.toString());
    }
}
