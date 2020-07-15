package com.php25.common.auditlog.manager;

import org.springframework.web.context.request.RequestAttributes;

/**
 * @author penghuiping
 * @date 2020/7/13 16:47
 */
public class AuditLogManagerAdaptor {

    AuditLogAssist auditLogAssist;

    public AuditLogManagerAdaptor(AuditLogAssist auditLogAssist) {
        this.auditLogAssist = auditLogAssist;
    }

    String getUserId(RequestAttributes requestAttributes) {
        return auditLogAssist.getUserId(requestAttributes);
    }

    String getTraceId(RequestAttributes requestAttributes) {
        return auditLogAssist.getTraceId(requestAttributes);
    }

    String getPreSpanId(RequestAttributes requestAttributes) {
        return auditLogAssist.getPreSpanId(requestAttributes);
    }
}
