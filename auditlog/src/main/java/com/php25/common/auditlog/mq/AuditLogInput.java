package com.php25.common.auditlog.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author penghuiping
 * @date 2020/7/13 14:55
 */
public interface AuditLogInput {

    String INPUT = "audit_log_input";

    @Input(AuditLogInput.INPUT)
    SubscribableChannel input();
}
