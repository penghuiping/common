package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.expression.Expression;

/**
 * @author penghuiping
 * @date 2021/12/28 23:01
 */
public class GroupLink implements Link {
    private final Expression expression;

    GroupLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("(%s)", expression.toString());
    }
}
