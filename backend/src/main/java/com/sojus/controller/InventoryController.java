package com.sojus.controller;

import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Software;
import com.sojus.service.InventoryService;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de hardware y software")
public class InventoryController {

    private final InventoryService inventoryService;

    // ---- Hardware ----

    @GetMapping("/hardware")
    @Operation(summary = "Listar todo el hardware")
    public ResponseEntity<List<Hardware>> findAllHardware() {
        return ResponseEntity.ok(inventoryService.findAllHardware());
    }

    @GetMapping("/hardware/{id}")
    @Operation(summary = "Obtener hardware por ID")
    public ResponseEntity<Hardware> findHardwareById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findHardwareById(id));
    }

    @PostMapping("/hardware")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Crear nuevo hardware")
    public ResponseEntity<Hardware> createHardware(@Valid @RequestBody Hardware hardware) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createHardware(hardware));
    }

    @PutMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Actualizar hardware")
    public ResponseEntity<Hardware> updateHardware(@PathVariable Long id, @Valid @RequestBody Hardware hardware) {
        return ResponseEntity.ok(inventoryService.updateHardware(id, hardware));
    }

    @DeleteMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Eliminar hardware (soft delete)")
    public ResponseEntity<Void> deleteHardware(@PathVariable Long id) {
        inventoryService.softDeleteHardware(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Software ----

    @GetMapping("/software")
    @Operation(summary = "Listar todo el software")
    public ResponseEntity<List<Software>> findAllSoftware() {
        return ResponseEntity.ok(inventoryService.findAllSoftware());
    }

    @GetMapping("/software/{id}")
    @Operation(summary = "Obtener software por ID")
    public ResponseEntity<Software> findSoftwareById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findSoftwareById(id));
    }

    @PostMapping("/software")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Crear nuevo software")
    public ResponseEntity<Software> createSoftware(@Valid @RequestBody Software software) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createSoftware(software));
    }

    @PutMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Actualizar software")
    public ResponseEntity<Software> updateSoftware(@PathVariable Long id, @Valid @RequestBody Software software) {
        return ResponseEntity.ok(inventoryService.updateSoftware(id, software));
    }

    @DeleteMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Eliminar software (soft delete)")
    public ResponseEntity<Void> deleteSoftware(@PathVariable Long id) {
        inventoryService.softDeleteSoftware(id);
        return ResponseEntity.noContent().build();
    }
}
