package com.php25.common.core.util;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.php25.common.core.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author: penghuiping
 * @date: 2018/8/15 10:12
 */
public abstract class XmlUtil {
    private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private static final XmlMapper xmlMapper = new XmlMapper();

    private static final PrettyPrinter PRETTY_PRINTER = new DefaultXmlPrettyPrinter();

    static {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        xmlMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> T fromXml(String xml, Class<T> cls) {
        AssertUtil.hasLength(xml, "xml不能为空");
        AssertUtil.notNull(cls, "cls不能为null");
        try {
            return xmlMapper.readValue(xml, cls);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("xml转换成实体对象时候失败", e);
        }
    }

    public static String toXml(Object obj) {
        AssertUtil.notNull(obj, "obj不能为null");
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("实体对象转换成xml失败", e);
        }
    }

    public static String toPrettyXml(Object obj) {
        AssertUtil.notNull(obj, "obj不能为null");
        try {
            return xmlMapper.writer(PRETTY_PRINTER).writeValueAsString(obj);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("实体对象转换成xml失败", e);
        }
    }

    public static <T> T fromXml(String xml, TypeReference<T> tTypeReference) {
        AssertUtil.hasLength(xml, "xml不能为空");
        AssertUtil.notNull(tTypeReference, "tTypeReference不能为null");
        try {
            return xmlMapper.readValue(xml, tTypeReference);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("xml转换成实体对象时候失败", e);
        }
    }
}
