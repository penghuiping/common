package com.php25.common.auditlog.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author penghuiping
 * @date 2020/7/13 14:54
 */
public interface AuditLogOutput {

    String OUTPUT = "audit_log_output";

    @Output(AuditLogOutput.OUTPUT)
    MessageChannel output();
}
