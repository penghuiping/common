package com.php25.common.db.sql.expression;

import com.php25.common.db.sql.column.Column;
import com.php25.common.db.sql.constant.DbConstant;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:20
 */
public class NotEqExpression extends BaseExpression {

    public NotEqExpression(Column column, Object value) {
        super(column, value);
    }

    @Override
    public String printSql() {
        return String.format("%s%s?", this.column.toString(), DbConstant.NOT_EQ);
    }

    @Override
    public List<Object> params() {
        return null;
    }
}
