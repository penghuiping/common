package com.php25.common.auditlog.repository;

import com.php25.common.auditlog.model.DbAuditLog;
import com.php25.common.db.Db;
import com.php25.common.db.repository.BaseDbRepositoryImpl;

/**
 * @author penghuiping
 * @date 2020/7/13 17:35
 */
public class AuditLogRepositoryImpl extends BaseDbRepositoryImpl<DbAuditLog, String> implements AuditLogRepository {


    public AuditLogRepositoryImpl(Db db) {
        super(db);
    }
}
