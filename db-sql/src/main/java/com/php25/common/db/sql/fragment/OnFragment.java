package com.php25.common.db.sql.fragment;

import com.php25.common.db.sql.expression.OnExpression;

/**
 * @author penghuiping
 * @date 2022/1/2 12:39
 */
public class OnFragment extends BaseFragment {

    private final OnExpression onExpression;

    public OnFragment(OnExpression onExpression) {
        this.onExpression = onExpression;
    }

    @Override
    public String printSql() {
        return onExpression.printSql();
    }


}
