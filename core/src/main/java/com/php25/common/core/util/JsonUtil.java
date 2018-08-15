package com.php25.common.core.util;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/8 17:05
 * @Description:
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final PrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> cls) {
        if (StringUtil.isBlank(json)) {
            throw new IllegalArgumentException("json不能为空");
        }

        if (null == cls) {
            throw new IllegalArgumentException("cls不能为null");
        }

        try {
            return objectMapper.readValue(json, cls);
        } catch (IOException e) {
            log.error("json解析出错", e);
            throw new RuntimeException("json解析出错", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference typeReference) {
        if (StringUtil.isBlank(json)) {
            throw new IllegalArgumentException("json不能为空");
        }

        if (null == typeReference) {
            throw new IllegalArgumentException("typeReference不能为null");
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("json解析出错", e);
            throw new RuntimeException("json解析出错", e);
        }
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        if (StringUtil.isBlank(json)) {
            throw new IllegalArgumentException("json不能为空");
        }

        if (null == javaType) {
            throw new IllegalArgumentException("javaType不能为null");
        }

        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            log.error("json解析出错", e);
            throw new RuntimeException("json解析出错", e);
        }
    }

    public static String toJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("json解析出错", e);
            throw new RuntimeException("json解析出错", e);
        }
    }

    public static String toPrettyJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return objectMapper.writer(prettyPrinter).writeValueAsString(obj);
        } catch (IOException e) {
            log.error("json解析出错", e);
            throw new RuntimeException("json解析出错", e);
        }
    }
}
