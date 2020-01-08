package com.php25.common.db.specification;

/**
 * @author penghuiping
 * @date 2016-04-12
 */
public enum Operator {
    /**
     * 对应sql中的 = 操作
     */
    EQ,

    /**
     * 对应sql中的 != 操作
     */
    NE,

    /**
     * 对应sql中的 like 操作
     */
    LIKE,

    /**
     * 对应sql中的 > 操作
     */
    GT,

    /**
     * 对应sql中的 < 操作
     */
    LT,

    /**
     * 对应sql中的 >= 操作
     */
    GTE,

    /**
     * 对应sql中的 <= 操作
     */
    LTE,

    /**
     * 对应sql中的 in 操作
     */
    IN,

    /**
     * 对应sql中的 not in 操作
     */
    NIN
}
