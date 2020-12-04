package com.php25.common.db.cnd.shard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author penghuiping
 * @date 2020/1/9 14:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface TableShard {

    /**
     * 获取逻辑表名
     *
     * @return 逻辑表名
     */
    String logicName() default "";

    /**
     * 获取物理表名 例如: ${datasource的bean名}.${物理表名}
     *
     * @return 物理表名
     */
    String[] physicName() default "";
}
