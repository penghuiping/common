package com.php25.common.db.sql.column;

import com.php25.common.db.sql.util.LambdaUtil;
import com.php25.common.db.sql.util.SFunction;

/**
 * @author penghuiping
 * @date 2021/12/28 18:47
 */
public class DefaultColumn implements Column {

    private String name;

    private DefaultColumn() {

    }

    static DefaultColumn of(String name) {
        DefaultColumn defaultColumn = new DefaultColumn();
        defaultColumn.name = name;
        return defaultColumn;
    }

    static <T, K> DefaultColumn of(SFunction<? super T, ? extends K> name) {
        return DefaultColumn.of(LambdaUtil.fieldNameFromLambda(name));
    }


    @Override
    public String toString() {
        return name;
    }
}
