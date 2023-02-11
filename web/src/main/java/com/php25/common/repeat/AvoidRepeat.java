package com.php25.common.repeat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author penghuiping
 * @date 2023/2/11 21:25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidRepeat {
    Class<? extends GetKeyStrategy> keyStrategy() default ShaHashKeyStrategy.class;

    /**
     * spring spEL表达式获取 请求唯一key {@link SpElKeyStrategy}
     *
     * @return 请求唯一key
     */
    String expression() default "";
}
