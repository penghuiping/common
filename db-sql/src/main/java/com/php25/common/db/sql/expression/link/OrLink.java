package com.php25.common.db.sql.expression.link;

import com.php25.common.db.sql.constant.DbConstant;
import com.php25.common.db.sql.expression.Expression;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:34
 */
public class OrLink extends BaseLink {
    private final Expression expression;

    OrLink(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String printSql() {
        return String.format(" %s %s", DbConstant.OR, expression.printSql());
    }

    @Override
    public List<Object> params() {
        return this.expression.params();
    }
}
