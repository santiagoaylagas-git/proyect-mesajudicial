package com.sojus.controller;

import com.sojus.domain.entity.AuditLog;
import com.sojus.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Auditoría", description = "Logs inmutables de cambios")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Últimos 100 registros de auditoría")
    public ResponseEntity<List<AuditLog>> findRecent() {
        return ResponseEntity.ok(auditService.findRecent());
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @Operation(summary = "Historial de una entidad específica")
    public ResponseEntity<List<AuditLog>> findByEntity(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.findByEntity(entityName, entityId));
    }
}
