package com.php25.common.mq;

import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/3/10 20:33
 */
public class Message {
    private String id;

    private Map<String, Object> headers;

    private Object body;

    public Message(String id, Map<String, Object> headers, Object body) {
        this.id = id;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
