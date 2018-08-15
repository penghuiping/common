package com.php25.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/15 10:12
 * @Description:
 */
public class XmlUtil {
    private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private static final XStream xstream = new XStream();

    public static <T> T fromXml(String xml, Class<T> cls) {
        xstream.processAnnotations(cls);
        return (T) xstream.fromXML(xml);
    }

    public static String toXml(Object obj) {
        xstream.processAnnotations(obj.getClass());
        return xstream.toXML(obj);
    }


    public static <T> T fromXml(String xml, TypeReference<T> tTypeReference) {
        xstream.processAnnotations(tTypeReference.getClass());
        return (T) xstream.fromXML(xml);
    }
}
