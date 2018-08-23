package com.php25.common.core.service;

import com.php25.common.core.util.JsonUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 用于加载properties数据
 * @author penghuiping
 * @date 2016/12/18
 */
@Service("resourceAwareService")
public class ResourceAwareServiceImpl implements ResourceAwareService {


    @Override
    public String loadProperties(String fileName) throws IOException {
        Resource r = new ClassPathResource(fileName);
        Properties p = PropertiesLoaderUtils.loadProperties(r);
        Map<String, String> map = new HashMap<>(16);
        Set<String> propertyNames = p.stringPropertyNames();
        for (String name : propertyNames) {
            map.put(name, p.getProperty(name));
        }
        return JsonUtil.toJson(map);
    }
}
