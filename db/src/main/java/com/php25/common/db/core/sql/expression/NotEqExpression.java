package com.php25.common.db.core.sql.expression;

import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.core.sql.column.Column;

/**
 * @author penghuiping
 * @date 2021/12/28 21:20
 */
public class NotEqExpression extends BaseExpression {

    public NotEqExpression(Column column, Object value) {
        super(column, value);
    }

    @Override
    public String toString() {
        return String.format("%s%s?", this.column.toString(), DbConstant.NOT_EQ);
    }
}
