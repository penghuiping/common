package com.php25.common.db;

import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.exception.DbException;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;


/**
 * @author penghuiping
 * @date 2018-08-23
 */
public class EntitiesScan {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    public void scanPackage(String... basePackages) {
        for (String basePackage : basePackages) {
            try {
                String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                        resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
                String basePackage0 = basePackage.replace(".", "/");
                for (Resource resource : resources) {
                    String path = resource.getURI().toString();
                    if (path.indexOf(basePackage0) > 0) {
                        String className = path.substring(path.indexOf(basePackage0)).split("\\.")[0].replace("/", ".");
                        Class<?> class0 = ClassUtils.getDefaultClassLoader().loadClass(className);
                        JdbcModelManager.getModelMeta(class0);
                    }
                }
            } catch (Exception e) {
                throw new DbException("Db在扫描包:" + basePackage + "出错", e);
            }
        }
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage));
    }
}