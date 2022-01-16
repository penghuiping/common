package com.php25.common.db.sql.expression;

import com.google.common.collect.Lists;
import com.php25.common.db.sql.column.Column;
import com.php25.common.db.sql.constant.DbConstant;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:10
 */
public class EqExpression extends BaseExpression {

    public EqExpression(Column column, Object value) {
        super(column, value);
    }

    @Override
    public List<Object> params() {
        return Lists.newArrayList(value);
    }

    @Override
    public String printSql() {
        return String.format("%s%s?", this.column.toString(), DbConstant.EQ);
    }
}
