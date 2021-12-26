package com.php25.common.db.core.sql;


import com.php25.common.db.util.LambdaUtil;

/**
 * @author penghuiping
 * @date 2021/12/26 15:17
 */
public class LambdaColumn<T, K> implements Column {
    private final SFunction<? super T, ? extends K> column;
    private final String entityAlias;


    public LambdaColumn(SFunction<? super T, ? extends K> column, String entityAlias) {
        this.column = column;
        this.entityAlias = entityAlias;
    }

    @Override
    public String getName() {
        return LambdaUtil.fieldNameFromLambda(column);
    }

    @Override
    public String getEntityAlias() {
        return this.entityAlias;
    }
}
