package com.php25.common.redis.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author penghuiping
 * @date 2022/7/24 18:38
 */
public class RedisTypeImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        EnableRedisManager annotation = importingClassMetadata.getAnnotations().get(EnableRedisManager.class).synthesize();
        RedisType redisType = annotation.type();
        String[] imports = null;

        if (RedisType.SINGLE.equals(redisType)) {
            imports = new String[]{SingleRedisConfig.class.getName(), RedisManagerAutoConfiguration.class.getName()};
        } else if (RedisType.SENTINEL.equals(redisType)) {
            imports = new String[]{SentinelRedisConfig.class.getName(), RedisManagerAutoConfiguration.class.getName()};
        } else if (RedisType.CLUSTER.equals(redisType)) {
            imports = new String[]{ClusterRedisConfig.class.getName(), RedisManagerAutoConfiguration.class.getName()};
        } else {
            imports = new String[]{MockRedisManagerAutoConfiguration.class.getName()};
        }
        return imports;
    }
}
