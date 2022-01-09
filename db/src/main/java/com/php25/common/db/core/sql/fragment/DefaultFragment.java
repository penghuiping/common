package com.php25.common.db.core.sql.fragment;

import com.php25.common.db.core.sql.column.Column;
import com.php25.common.db.core.sql.expression.OnExpression;
import com.php25.common.db.core.sql.expression.link.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/1 21:21
 */
public class DefaultFragment extends BaseFragment {
    private final List<Fragment> tables = new ArrayList<>();

    public DefaultFragment from(Class<?> entity) {
        return this.from(entity, null);
    }

    public DefaultFragment join(Class<?> entity) {
        return this.join(entity, null);
    }

    public DefaultFragment from(Class<?> entity, String alias) {
        FromFragment fromFragment = new FromFragment(entity, alias);
        this.tables.add(fromFragment);
        return this;
    }

    public DefaultFragment join(Class<?> entity, String alias) {
        JoinFragment joinFragment = new JoinFragment(entity, alias);
        this.tables.add(joinFragment);
        return this;
    }

    public DefaultFragment on(Column left, Column right) {
        OnExpression onExpression = new OnExpression(left, right);
        OnFragment onFragment = new OnFragment(onExpression);
        this.tables.add(onFragment);
        return this;
    }

    public DefaultFragment where(Link link) {
        WhereFragment whereFragment = new WhereFragment(link);
        this.tables.add(whereFragment);
        return this;
    }

    @Override
    public String printSql() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tables.size(); i++) {
            Fragment table = tables.get(i);
            sb.append(table.printSql());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public List<Object> params() {
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            Fragment table = tables.get(i);
            if (null != table.params() && !table.params().isEmpty()) {
                params.addAll(table.params());
            }
        }
        return params;
    }
}

