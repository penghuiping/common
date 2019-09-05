package com.php25.common.core.util;

import com.php25.common.core.exception.Exceptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author: penghuiping
 * @date: 2019/9/3 10:15
 * @description:
 */
public abstract class PropertiesUtil {

    /**
     * 加载classpath中的properties配置文件内容，并以json字符串的形式返回
     *
     * @param fileName classpath中的properties配置文件名
     * @return string
     */
    public static String loadProperties(String fileName) {
        try {
            Resource r = new ClassPathResource(fileName);
            Properties p = PropertiesLoaderUtils.loadProperties(r);
            Map<String, String> map = new HashMap<>(16);
            Set<String> propertyNames = p.stringPropertyNames();
            for (String name : propertyNames) {
                map.put(name, p.getProperty(name));
            }
            return JsonUtil.toJson(map);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("加载properties文件出错", e);
        }
    }
}
