package com.php25.common.db.sql.expression;

import com.php25.common.db.sql.column.Column;
import com.php25.common.db.sql.constant.DbConstant;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:10
 */
public class OnExpression extends BaseExpression {

    public OnExpression(Column left, Column right) {
        super(left, right);
    }

    @Override
    public String printSql() {
        return String.format("%s %s%s%s", DbConstant.ON, this.column.toString(), DbConstant.EQ, this.value.toString());
    }

    @Override
    public List<Object> params() {
        return null;
    }
}
