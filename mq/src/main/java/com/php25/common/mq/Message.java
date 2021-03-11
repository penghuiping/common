package com.php25.common.mq;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @JsonIgnore
    public void addHeader(String key, Object value) {
        this.getHeaders().put(key, value);
    }

    @JsonIgnore
    public String getId() {
        Object id = this.getHeaders().get("id");
        return null == id ? "" : id.toString();
    }

    @JsonIgnore
    public void setId(String id) {
        this.headers.put("id", id);
    }

    @JsonIgnore
    public String getQueue() {
        Object queue = this.getHeaders().get("queue");
        return null == queue ? "" : queue.toString();
    }

    @JsonIgnore
    public void setQueue(String queue) {
        this.headers.put("queue", queue);
    }

    @JsonIgnore
    public String getGroup() {
        Object group = this.getHeaders().get("group");
        return null == group ? "" : group.toString();
    }

    @JsonIgnore
    public void setGroup(String group) {
        this.headers.put("group", group);
    }

    @JsonIgnore
    public String getErrorInfo() {
        Object error = this.getHeaders().get("error");
        return error == null ? "" : error.toString();
    }

    @JsonIgnore
    public void setErrorInfo(String errorInfo) {
        this.headers.put("error", errorInfo);
    }
}
