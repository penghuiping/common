package com.php25.common.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.php25.common.core.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author: penghuiping
 * @date: 2018/8/8 17:05
 */
public abstract class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final PrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.setDateFormat(new SimpleDateFormat(TimeUtil.STD_FORMAT));
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        objectMapper.registerModule(timeModule);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

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
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtil.isBlank(json)) {
            throw new IllegalArgumentException("json不能为空");
        }

        if (null == typeReference) {
            throw new IllegalArgumentException("typeReference不能为null");
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
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
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    public static String toJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    public static String toPrettyJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return objectMapper.writer(PRETTY_PRINTER).writeValueAsString(obj);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    //时间序列化时变为时间戳
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(localDateTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    //时间戳反序列化时间
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            Long timestamp = jsonParser.getLongValue();
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        }
    }
}
