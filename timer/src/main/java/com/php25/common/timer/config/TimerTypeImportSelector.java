package com.php25.common.timer.config;

import com.php25.common.redis.config.MockRedisManagerAutoConfiguration;
import com.php25.common.redis.config.RedisManagerAutoConfiguration;
import com.php25.common.redis.config.SentinelRedisConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author penghuiping
 * @date 2023/9/9 11:36
 */
public class TimerTypeImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        EnableTimer annotation = importingClassMetadata.getAnnotations().get(EnableTimer.class).synthesize();
        TimerType timerType = annotation.type();
        String[] imports = null;

        if (TimerType.USING_DATABASE.equals(timerType)) {
            imports = new String[]{TimerDatabaseAutoConfiguration.class.getName()};
        } else if (TimerType.USING_REDIS.equals(timerType)) {
            imports = new String[]{SentinelRedisConfig.class.getName(), RedisManagerAutoConfiguration.class.getName()};
        } else {
            imports = new String[]{MockRedisManagerAutoConfiguration.class.getName()};
        }
        return imports;
    }
}
