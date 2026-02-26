package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private String prioridad; // ALTA, MEDIA, BAJA

    @NotNull(message = "El juzgado es obligatorio")
    private Long juzgadoId;

    private Long hardwareId;

    private String canal; // WEB, PORTAL, EMAIL
}
