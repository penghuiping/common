package com.php25.common.db.core.sql.expression.link;

import com.php25.common.db.core.sql.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2021/12/28 21:47
 */
public class DefaultLink implements Link {

    private final List<Link> links = new ArrayList<>();

    public DefaultLink and(Expression expression) {
        this.links.add(new AndLink(expression));
        return this;
    }

    public DefaultLink or(Expression expression) {
        this.links.add(new OrLink(expression));
        return this;
    }

    public DefaultLink group(Expression expression) {
        this.links.add(new GroupLink(expression));
        return this;
    }

    public DefaultLink cnd(Expression expression) {
        this.links.add(new CndLink(expression));
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            sb.append(link.toString());
        }
        return sb.toString();
    }
}
