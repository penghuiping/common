package com.php25.common.db.core.sql.expression;

import com.php25.common.db.core.sql.column.Column;

/**
 * @author penghuiping
 * @date 2021/12/28 21:00
 */
public abstract class Expressions {

    public static Expression eq(Column column, Object value) {
        return new EqExpression(column, value);
    }

    public static Expression notEq() {
        return null;
    }

    public static Expression great() {
        return null;
    }

    public static Expression greatEq() {
        return null;
    }

    public static Expression less() {
        return null;
    }

    public static Expression lessEq() {
        return null;
    }

    public static Expression like() {
        return null;
    }

    public static Expression notLike() {
        return null;
    }

    public static Expression isNull() {
        return null;
    }

    public static Expression isNotNull() {
        return null;
    }

    public static Expression in() {
        return null;
    }

    public static Expression notIn() {
        return null;
    }

    public static Expression between() {
        return null;
    }

    public static Expression notBetween() {
        return null;
    }
}
