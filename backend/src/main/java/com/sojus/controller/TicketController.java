package com.sojus.controller;

import com.sojus.domain.entity.User;
import com.sojus.dto.StatusChangeRequest;
import com.sojus.dto.TicketRequest;
import com.sojus.dto.TicketResponse;
import com.sojus.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Mesa de Ayuda", description = "Gestión de tickets de soporte")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Listar todos los tickets")
    public ResponseEntity<List<TicketResponse>> findAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.findAllForUser(user));
    }

    @GetMapping("/my")
    @Operation(summary = "Listar tickets asignados al usuario autenticado")
    public ResponseEntity<List<TicketResponse>> findMyTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.findByUser(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket por ID")
    public ResponseEntity<TicketResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Crear nuevo ticket")
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody TicketRequest request,
            @AuthenticationPrincipal User user) {
        TicketResponse response = ticketService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Cambiar estado del ticket")
    public ResponseEntity<TicketResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusChangeRequest request,
            @AuthenticationPrincipal User user) {
        TicketResponse response = ticketService.changeStatus(id, request, user.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar ticket (soft delete)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ticketService.softDelete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
