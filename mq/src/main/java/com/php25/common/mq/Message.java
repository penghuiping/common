package com.php25.common.mq;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/3/10 20:33
 */
public class Message {

    private final Map<String, Object> headers = new HashMap<>(16);

    private Object body;

    public Message(String id, String queue, Object body) {
        this(id, queue, null, body);
    }

    public Message(String id, String queue, String group, Object body) {
        this.setId(id);
        this.setQueue(queue);
        this.setGroup(group);
        this.body = body;
    }


    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map.Entry<String, Object> getHeader(String key) {
        return ImmutablePair.of(key, this.headers.get(key));
    }


    public void addHeader(String key, Object value) {
        this.getHeaders().put(key, value);
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getId() {
        return this.getHeaders().get("id").toString();
    }

    public void setId(String id) {
        this.headers.put("id", id);
    }

    public String getQueue() {
        return this.getHeaders().get("queue").toString();
    }

    public void setQueue(String queue) {
        this.headers.put("queue", queue);
    }

    public String getGroup() {
        return this.getHeaders().get("group").toString();
    }

    public void setGroup(String group) {
        this.headers.put("group", group);
    }

    public String getErrorInfo() {
        return this.getHeaders().get("error").toString();
    }

    public void setErrorInfo(String errorInfo) {
        this.headers.put("error", errorInfo);
    }
}
