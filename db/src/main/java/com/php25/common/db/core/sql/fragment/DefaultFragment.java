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
public class DefaultFragment implements Fragment {
    private final List<Fragment> tables = new ArrayList<>();

    public DefaultFragment from(Class<?> entity) {
        this.tables.add(new FromFragment(entity, null));
        return this;
    }

    public DefaultFragment join(Class<?> entity) {
        this.tables.add(new JoinFragment(entity, null));
        return this;
    }

    public DefaultFragment from(Class<?> entity, String alias) {
        this.tables.add(new FromFragment(entity, alias));
        return this;
    }

    public DefaultFragment join(Class<?> entity, String alias) {
        this.tables.add(new JoinFragment(entity, alias));
        return this;
    }

    public DefaultFragment on(Column left, Column right) {
        this.tables.add(new OnFragment(new OnExpression(left, right)));
        return this;
    }

    public DefaultFragment where(Link link) {
        this.tables.add(new WhereFragment(link));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tables.size(); i++) {
            Fragment table = tables.get(i);
            sb.append(table.toString());
            sb.append(" ");
        }
        return sb.toString();
    }
}

