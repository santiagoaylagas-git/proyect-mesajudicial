package com.sojus.controller;

import com.sojus.domain.entity.Circunscripcion;
import com.sojus.domain.entity.Juzgado;
import com.sojus.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Estructura Territorial", description = "Circunscripciones, Distritos, Edificios y Juzgados")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/circunscripciones")
    @Operation(summary = "Listar circunscripciones con su jerarquía")
    public ResponseEntity<List<Circunscripcion>> findCircunscripciones() {
        return ResponseEntity.ok(locationService.findAllCircunscripciones());
    }

    @GetMapping("/juzgados")
    @Operation(summary = "Listar todos los juzgados activos")
    public ResponseEntity<List<Juzgado>> findJuzgados() {
        return ResponseEntity.ok(locationService.findAllJuzgados());
    }

    @GetMapping("/edificios/{edificioId}/juzgados")
    @Operation(summary = "Listar juzgados de un edificio")
    public ResponseEntity<List<Juzgado>> findJuzgadosByEdificio(@PathVariable Long edificioId) {
        return ResponseEntity.ok(locationService.findJuzgadosByEdificio(edificioId));
    }
}
