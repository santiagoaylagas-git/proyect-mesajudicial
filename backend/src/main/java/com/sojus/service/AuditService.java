package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLog> findRecent() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findByEntity(String entityName, Long entityId) {
        return auditLogRepository.findAllByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId);
    }
}
