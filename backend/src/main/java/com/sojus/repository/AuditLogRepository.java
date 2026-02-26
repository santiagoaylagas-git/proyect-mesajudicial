package com.sojus.repository;

import com.sojus.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId);
    List<AuditLog> findTop100ByOrderByTimestampDesc();
}
