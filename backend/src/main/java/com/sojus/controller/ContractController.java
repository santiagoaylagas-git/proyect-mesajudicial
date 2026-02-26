package com.sojus.controller;

import com.sojus.domain.entity.Contract;
import com.sojus.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contratos", description = "Gestión de contratos y alertas de vencimiento")
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar contratos activos")
    public ResponseEntity<List<Contract>> findAll() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener contrato por ID")
    public ResponseEntity<Contract> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo contrato")
    public ResponseEntity<Contract> create(@Valid @RequestBody Contract contract) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.create(contract));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar contrato")
    public ResponseEntity<Contract> update(@PathVariable Long id, @Valid @RequestBody Contract contract) {
        return ResponseEntity.ok(contractService.update(id, contract));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desactivar contrato")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        contractService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Contratos próximos a vencer", description = "Enviar ?days=30 para ver contratos que vencen en 30 días")
    public ResponseEntity<List<Contract>> findExpiring(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(contractService.findExpiringSoon(days));
    }
}
