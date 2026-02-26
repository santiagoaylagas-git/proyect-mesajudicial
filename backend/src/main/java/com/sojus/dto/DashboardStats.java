package com.sojus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private long ticketsAbiertos;
    private long ticketsCerradosMes;
    private long ticketsPrioridadAlta;
    private long totalHardware;
    private long totalSoftware;
    private long contratosVigentes;
    private long contratosProximosVencer;
}
