package com.sojus.repository;

import com.sojus.domain.entity.Ticket;
import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByDeletedFalse();

    List<Ticket> findAllByStatusAndDeletedFalse(TicketStatus status);

    List<Ticket> findAllByPrioridadAndDeletedFalse(Priority prioridad);

    List<Ticket> findAllBySolicitanteIdAndDeletedFalse(Long solicitanteId);

    List<Ticket> findAllByTecnicoAsignadoIdAndDeletedFalse(Long tecnicoId);

    long countByStatusAndDeletedFalse(TicketStatus status);

    long countByPrioridadAndStatusNotAndDeletedFalse(Priority prioridad, TicketStatus status);

    boolean existsByHardwareAfectadoIdAndStatusNotAndDeletedFalse(Long hardwareId, TicketStatus excludedStatus);
}
