package com.php25.common.auditlog.repository;

import com.php25.common.auditlog.model.DbAuditLog;
import com.php25.common.db.repository.BaseDbRepository;

/**
 * @author penghuiping
 * @date 2020/7/13 17:32
 */
public interface AuditLogRepository extends BaseDbRepository<DbAuditLog, String> {
}
