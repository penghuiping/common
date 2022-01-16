package com.php25.common.db.sql;


import com.php25.common.db.sql.expression.Expression;
import com.php25.common.db.sql.expression.link.DefaultLink;
import com.php25.common.db.sql.fragment.Fragment;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.junit.Test;

import static com.php25.common.db.sql.column.Columns.col;
import static com.php25.common.db.sql.expression.Expressions.eq;
import static com.php25.common.db.sql.expression.link.Links.cnd;
import static com.php25.common.db.sql.expression.link.Links.group;
import static com.php25.common.db.sql.fragment.Fragments.from;


/**
 * @author penghuiping
 * @date 2020/12/2 17:46
 */
public class SqlTest {
    private static final Logger log = LoggerFactory.getLogger(SqlTest.class);

    @Test
    public void test() {
        Expression expression0 = eq(col("a", Customer::getUsername), "jack");
        Expression expression1 = eq(col("a", Customer::getAge), 10);
        DefaultLink link0 = group(cnd(expression0).and(expression1));
        Expression expression2 = eq(col("b", Company::getName), "Google");
        DefaultLink link = link0.and(expression2);
        Fragment fragment = from(Customer.class, "a").join(Company.class, "b")
                .on(col("a", Customer::getCompanyId), col("b", Company::getId))
                .where(link);

        System.out.println(fragment.printSql());
        System.out.println(fragment.params());
    }


}
