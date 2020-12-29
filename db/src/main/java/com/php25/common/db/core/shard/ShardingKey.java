package com.php25.common.db.core.shard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author penghuiping
 * @date 2020/12/1 14:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface ShardingKey {

    Class<? extends ShardRule> shardRule() default ShardRuleHashBased.class;
}
