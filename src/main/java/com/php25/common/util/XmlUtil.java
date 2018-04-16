package com.php25.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by penghuiping on 2017/4/20.
 */
public class XmlUtil {
    public static String mapToXMLString(SortedMap<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder("<xml>");
        Set<Map.Entry<String, String>> set = params.entrySet();
        Iterator<Map.Entry<String, String>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append("<" + key + ">" + value + "</" + key + ">");
        }
        sb.append("</xml>");
        return new String(sb.toString().getBytes(), "UTF-8");
    }

    public static Map<String, String> getMapFromXML(String xmlString) throws Exception {
        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = null;
        if (xmlString != null && !xmlString.trim().equals("")) {
            is = new ByteArrayInputStream(xmlString.getBytes("utf-8"));
        }
        Document document = builder.parse(is);

        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, String> map = new HashMap<String, String>();
        int i = 0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if (node instanceof org.w3c.dom.Element) {
                map.put(node.getNodeName(), node.getTextContent());
            }
            i++;
        }
        return map;
    }
}
