package com.php25.common.db.sql.expression.link;


import com.php25.common.db.sql.expression.Expression;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 23:01
 */
public class GroupLink extends BaseLink {
    private final Expression expression;

    GroupLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String printSql() {
        return String.format("(%s)", expression.printSql());
    }

    @Override
    public List<Object> params() {
        return this.expression.params();
    }
}
