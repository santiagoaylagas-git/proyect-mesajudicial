package com.sojus.service;

import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.TicketStatus;
import com.sojus.dto.DashboardStats;
import com.sojus.repository.ContractRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.SoftwareRepository;
import com.sojus.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final HardwareRepository hardwareRepository;
    private final SoftwareRepository softwareRepository;
    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        long abiertos = ticketRepository.countByStatusAndDeletedFalse(TicketStatus.SOLICITADO)
                + ticketRepository.countByStatusAndDeletedFalse(TicketStatus.ASIGNADO)
                + ticketRepository.countByStatusAndDeletedFalse(TicketStatus.EN_CURSO);
        long cerrados = ticketRepository.countByStatusAndDeletedFalse(TicketStatus.CERRADO);
        long prioridadAlta = ticketRepository.countByPrioridadAndStatusNotAndDeletedFalse(
                Priority.ALTA, TicketStatus.CERRADO);

        return DashboardStats.builder()
                .ticketsAbiertos(abiertos)
                .ticketsCerradosMes(cerrados)
                .ticketsPrioridadAlta(prioridadAlta)
                .totalHardware(hardwareRepository.countByDeletedFalse())
                .totalSoftware(softwareRepository.countByDeletedFalse())
                .contratosVigentes(contractRepository.countByActiveTrue())
                .contratosProximosVencer(contractRepository.countByFechaFinBeforeAndActiveTrue(
                        LocalDate.now().plusDays(30)))
                .build();
    }
}
