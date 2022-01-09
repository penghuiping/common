package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.expression.Expression;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/1 22:09
 */
public class CndLink extends BaseLink {
    private final Expression expression;

    CndLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String printSql() {
        return expression.printSql();
    }

    @Override
    public List<Object> params() {
        return expression.params();
    }
}
