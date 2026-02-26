package com.sojus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String asunto;
    private String descripcion;
    private String status;
    private String prioridad;
    private String juzgadoNombre;
    private String solicitanteNombre;
    private String tecnicoNombre;
    private String hardwareInventario;
    private String bitacora;
    private String canal;
    private String createdAt;
    private String updatedAt;
    private String closedAt;
}
