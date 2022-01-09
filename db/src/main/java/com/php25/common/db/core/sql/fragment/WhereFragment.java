package com.php25.common.db.core.sql.fragment;

import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.core.sql.expression.link.Link;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/1 21:47
 */
public class WhereFragment extends BaseFragment {
    private final Link link;

    public WhereFragment(Link link) {
        this.link = link;
    }

    @Override
    public String printSql() {
        return String.format("%s %s", DbConstant.WHERE, link.printSql());
    }

    @Override
    public List<Object> params() {
        return link.params();
    }
}
