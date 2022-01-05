package com.php25.common.db.core.sql.expression;

import com.php25.common.db.core.sql.column.Column;

/**
 * @author penghuiping
 * @date 2021/12/28 20:38
 */
public abstract class BaseExpression implements Expression {

    protected Column column;

    protected Object value;

    public BaseExpression(Column column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
