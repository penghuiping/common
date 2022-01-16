package com.php25.common.db.sql.expression.link;


import com.php25.common.db.sql.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:47
 */
public class DefaultLink extends BaseLink {

    private final List<Link> links = new ArrayList<>();

    public DefaultLink and(Expression expression) {
        AndLink andLink = new AndLink(expression);
        this.links.add(andLink);
        return this;
    }

    public DefaultLink or(Expression expression) {
        OrLink orLink = new OrLink(expression);
        this.links.add(orLink);
        return this;
    }

    public DefaultLink group(Expression expression) {
        GroupLink groupLink = new GroupLink(expression);
        this.links.add(groupLink);
        return this;
    }

    public DefaultLink cnd(Expression expression) {
        CndLink cndLink = new CndLink(expression);
        this.links.add(cndLink);
        return this;
    }


    @Override
    public String printSql() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            sb.append(link.printSql());
        }
        return sb.toString();
    }

    @Override
    public List<Object> params() {
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            params.addAll(link.params());
        }
        return params;
    }
}
