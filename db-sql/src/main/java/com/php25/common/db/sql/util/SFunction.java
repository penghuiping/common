package com.php25.common.db.sql.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author penghuiping
 * @date 2021/12/25 21:28
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {

}
