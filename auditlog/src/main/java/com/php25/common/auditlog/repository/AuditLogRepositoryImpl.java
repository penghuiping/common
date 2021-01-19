package com.php25.common.auditlog.repository;

import com.php25.common.auditlog.model.DbAuditLog;
import com.php25.common.db.DbType;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author penghuiping
 * @date 2020/7/13 17:35
 */
public class AuditLogRepositoryImpl extends BaseDbRepositoryImpl<DbAuditLog, String> implements AuditLogRepository {


    public AuditLogRepositoryImpl(JdbcTemplate jdbcTemplate, DbType dbType) {
        super(jdbcTemplate, dbType);
    }
}
