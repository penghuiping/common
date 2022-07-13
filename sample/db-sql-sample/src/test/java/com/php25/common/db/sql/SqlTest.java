package com.php25.common.db.sql;


import com.php25.common.db.sql.expression.Expression;
import com.php25.common.db.sql.fragment.Fragment;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Expression expression2 = eq(col("b", Company::getName), "Google");
        Fragment fragment = from(Customer.class, "a").join(Company.class, "b")
                .on(col("a", Customer::getCompanyId), col("b", Company::getId))
                .where(group(cnd(expression0).and(expression1)).and(expression2));

        System.out.println(fragment.printSql());
        System.out.println(fragment.params());
    }


}
