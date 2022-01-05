package com.php25.common.db.core.sql.column;

import com.php25.common.db.core.sql.SFunction;

/**
 * @author penghuiping
 * @date 2021/12/28 20:42
 */
public abstract class Columns {

    public static AsColumn as(Column column) {
        return AsColumn.of(column);
    }

    public static AsColumn as(Column column, String alias) {
        return AsColumn.of(column, alias);
    }

    public static DefaultColumn col(String name) {
        return DefaultColumn.of(name);
    }

    public static <T, K> DefaultColumn col(SFunction<? super T, ? extends K> name) {
        return DefaultColumn.of(name);
    }

    public static DotColumn col(String entityAlias, String name) {
        return DotColumn.of(entityAlias, name);
    }

    public static <T, K> DotColumn col(String entityAlias, SFunction<? super T, ? extends K> name) {
        return DotColumn.of(entityAlias, name);
    }
}
