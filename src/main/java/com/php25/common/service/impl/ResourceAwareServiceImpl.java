package com.php25.common.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.service.ResourceAwareService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by penghuiping on 2016/12/18.
 */
@Service("resourceAwareService")
public class ResourceAwareServiceImpl implements ResourceAwareService {

    @Autowired
    ObjectMapper objectMapper;

    public String loadBeetlProperties() throws IOException {
        Resource r = new ClassPathResource("beetl.properties");
        Properties p = PropertiesLoaderUtils.loadProperties(r);
        Map<String, String> map = new HashMap<>();
        Set<String> propertyNames = p.stringPropertyNames();
        for (String name : propertyNames) {
            map.put(name, p.getProperty(name));
        }
        return objectMapper.writeValueAsString(map);
    }
}
