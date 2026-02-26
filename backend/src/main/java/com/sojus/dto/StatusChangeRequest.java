package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusChangeRequest {
    @NotBlank(message = "El nuevo estado es obligatorio")
    private String status; // SOLICITADO, ASIGNADO, EN_CURSO, CERRADO

    private Long tecnicoId;

    private String comentario;
}
