package com.sojus.controller;

import com.sojus.dto.UserResponse;
import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Usuarios", description = "ABM de usuarios del sistema")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar usuarios activos")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAllAsDto());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findByIdAsDto(id));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuarios por rol")
    public ResponseEntity<List<UserResponse>> findByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.findByRoleAsDto(RoleName.valueOf(role)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAsDto(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateAsDto(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
