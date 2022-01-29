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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author penghuiping
 * @date 2018/8/8 17:05
 */
public abstract class JsonUtil {
    private static final PrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(TimeUtil.STD_FORMAT));
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        OBJECT_MAPPER.registerModule(timeModule);
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        if (StringUtil.isBlank(json)) {
            throw new IllegalArgumentException("json不能为空");
        }

        if (null == cls) {
            throw new IllegalArgumentException("cls不能为null");
        }

        try {
            return OBJECT_MAPPER.readValue(json, cls);
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
            return OBJECT_MAPPER.readValue(json, typeReference);
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
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    public static String toJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }

    public static String toPrettyJson(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException("obj不能为null");
        }
        try {
            return OBJECT_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(obj);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("json解析出错", e);
        }
    }


    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(localDateTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(TimeUtil.STD_FORMAT)));
        }
    }


    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getValueAsString();
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(TimeUtil.STD_FORMAT));
        }
    }
}
