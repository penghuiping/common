package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.core.sql.expression.Expression;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:33
 */
public class AndLink extends BaseLink {
    private final Expression expression;

    AndLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String printSql() {
        return String.format(" %s %s", DbConstant.AND, expression.printSql());
    }

    @Override
    public List<Object> params() {
        return expression.params();
    }
}
