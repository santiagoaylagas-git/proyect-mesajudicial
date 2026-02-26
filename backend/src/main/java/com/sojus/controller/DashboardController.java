package com.sojus.controller;

import com.sojus.dto.DashboardStats;
import com.sojus.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
@Tag(name = "Dashboard", description = "Estadísticas y métricas")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas del dashboard")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
