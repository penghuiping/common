package com.php25.common.auditlog.dto;


/**
 * @author penghuiping
 * @date 2020/7/13 14:57
 */
public class AuditLog<T> {

    /**
     * 链路id，一个链路包含多个环节
     */
    private String traceId;

    /**
     * 上一个环节id
     */
    private String preSpanId;

    /**
     * 当前环节id
     */
    private String spanId;

    /**
     * 进入当前环节的时间
     */
    private Long startTime;

    /**
     * 离开当前环节的时间
     */
    private Long endTime;

    /**
     * 需要采集与记录的内容
     */
    private T content;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPreSpanId() {
        return preSpanId;
    }

    public void setPreSpanId(String preSpanId) {
        this.preSpanId = preSpanId;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
