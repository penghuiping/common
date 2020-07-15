package com.php25.common.auditlog.manager;

import com.php25.common.auditlog.dto.AuditLog;
import com.php25.common.auditlog.model.DbAuditLog;
import com.php25.common.auditlog.mq.AuditLogProcessor;
import com.php25.common.auditlog.repository.AuditLogRepository;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.core.util.JsonUtil;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2020/7/13 14:27
 */
public class AuditLogManagerImpl<T> implements AuditLogManager<T> {

    private final AuditLogProcessor auditLogProcessor;

    private final AuditLogManagerAdaptor auditLogManagerAdaptor;

    private final SnowflakeIdWorker snowflakeIdWorker;

    private final AuditLogRepository auditLogRepository;

    public AuditLogManagerImpl(AuditLogProcessor auditLogProcessor, AuditLogManagerAdaptor auditLogManagerAdaptor, SnowflakeIdWorker snowflakeIdWorker, AuditLogRepository auditLogRepository) {
        this.auditLogProcessor = auditLogProcessor;
        this.auditLogManagerAdaptor = auditLogManagerAdaptor;
        this.snowflakeIdWorker = snowflakeIdWorker;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void log(String traceId, String preSpanId, Long startTime, Long endTime, T content) {
        AuditLog<T> auditLog = new AuditLog<>();
        auditLog.setTraceId(traceId);
        auditLog.setPreSpanId(preSpanId);
        auditLog.setSpanId(this.generateId());
        auditLog.setStartTime(startTime);
        auditLog.setEndTime(endTime);
        auditLog.setContent(content);
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "auditlogManager.auditLogHandle");
        GenericMessage<AuditLog<T>> message = new GenericMessage<>(auditLog, headers);
        auditLogProcessor.output().send(message);
    }


    @StreamListener(value = AuditLogProcessor.INPUT, condition = "headers['type']=='auditlogManager.auditLogHandle'")
    public void auditLogHandle(GenericMessage<AuditLog<T>> message) {
        AuditLog<T> auditLog = message.getPayload();
        DbAuditLog dbAuditLog = new DbAuditLog();
        dbAuditLog.setSpanId(auditLog.getSpanId());
        dbAuditLog.setPreSpanId(auditLog.getPreSpanId());
        dbAuditLog.setTraceId(auditLog.getTraceId());
        dbAuditLog.setStartTime(auditLog.getStartTime());
        dbAuditLog.setEndTime(auditLog.getEndTime());
        dbAuditLog.setContent(JsonUtil.toJson(auditLog.getContent()));
        dbAuditLog.setNew(true);
        auditLogRepository.save(dbAuditLog);
    }


    @Override
    public String getTraceId(RequestAttributes requestAttributes) {
        return auditLogManagerAdaptor.getTraceId(requestAttributes);
    }

    @Override
    public String getPreSpanId(RequestAttributes requestAttributes) {
        return auditLogManagerAdaptor.getPreSpanId(requestAttributes);
    }

    @Override
    public String getUserId(RequestAttributes requestAttributes) {
        return auditLogManagerAdaptor.getUserId(requestAttributes);
    }


    @Override
    public String generateId() {
        return snowflakeIdWorker.nextId()+"";
    }
}
