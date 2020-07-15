package com.php25.common.auditlog.model;

import com.php25.common.db.cnd.annotation.Column;
import com.php25.common.db.cnd.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

/**
 * @author penghuiping
 * @date 2020/7/13 17:32
 */
@Table("t_audit_log")
public class DbAuditLog implements Persistable<String> {

    /**
     * 当前环节id
     */
    @Id
    @Column("span_id")
    private String spanId;

    /**
     * 链路id，一个链路包含多个环节
     */
    @Column("trace_id")
    private String traceId;

    /**
     * 上一个环节id
     */
    @Column("pre_span_id")
    private String preSpanId;


    /**
     * 进入当前环节的时间
     */
    @Column("start_time")
    private Long startTime;

    /**
     * 离开当前环节的时间
     */
    @Column("end_time")
    private Long endTime;

    /**
     * 需要采集与记录的内容
     */
    @Column
    private String content;

    private Boolean isNew;

    @Override
    public String getId() {
        return getSpanId();
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getPreSpanId() {
        return preSpanId;
    }

    public void setPreSpanId(String preSpanId) {
        this.preSpanId = preSpanId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
