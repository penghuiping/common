package com.php25.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: penghuiping
 * @date: 2018/8/15 10:12
 */
public class XmlUtil {
    private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private static final XStream XSTREAM = new XStream();

    public static <T> T fromXml(String xml, Class<T> cls) {
        XSTREAM.processAnnotations(cls);
        return (T) XSTREAM.fromXML(xml);
    }

    public static String toXml(Object obj) {
        XSTREAM.processAnnotations(obj.getClass());
        return XSTREAM.toXML(obj);
    }


    public static <T> T fromXml(String xml, TypeReference<T> tTypeReference) {
        XSTREAM.processAnnotations(tTypeReference.getClass());
        return (T) XSTREAM.fromXML(xml);
    }
}
