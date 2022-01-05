package com.php25.common.db.core.sql.fragment;

import com.php25.common.db.core.sql.expression.OnExpression;

/**
 * @author penghuiping
 * @date 2022/1/2 12:39
 */
public class OnFragment implements Fragment {

    private final OnExpression onExpression;

    public OnFragment(OnExpression onExpression) {
        this.onExpression = onExpression;
    }

    @Override
    public String toString() {
        return onExpression.toString();
    }
}
