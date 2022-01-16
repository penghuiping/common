package com.php25.common.db.sql.column;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import com.php25.common.db.sql.util.LambdaUtil;
import com.php25.common.db.sql.util.SFunction;

/**
 * @author penghuiping
 * @date 2021/12/28 15:35
 */
public class DotColumn implements Column {
    private String name;
    private String entityAlias;

    private DotColumn() {

    }

    static DotColumn of(String entityAlias, String name) {
        DotColumn dotColumn = new DotColumn();
        dotColumn.name = name;
        dotColumn.entityAlias = entityAlias;
        return dotColumn;
    }

    static <T, K> DotColumn of(String entityAlias, SFunction<? super T, ? extends K> name) {
        Class<?> entityClass = LambdaUtil.classFromLambda(name);
        String dbName = JdbcModelCacheManager.getDbColumnByClassColumn(entityClass, LambdaUtil.fieldNameFromLambda(name));
        return DotColumn.of(entityAlias, dbName);
    }

    @Override
    public String toString() {
        if (StringUtil.isNotBlank(entityAlias)) {
            return entityAlias + "." + name;
        }
        return name;
    }
}
