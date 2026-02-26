package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Juzgado;
import com.sojus.domain.entity.Ticket;
import com.sojus.domain.entity.User;
import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.RoleName;
import com.sojus.domain.enums.TicketStatus;
import com.sojus.dto.StatusChangeRequest;
import com.sojus.dto.TicketRequest;
import com.sojus.dto.TicketResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.JuzgadoRepository;
import com.sojus.repository.TicketRepository;
import com.sojus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TicketService {

        private final TicketRepository ticketRepository;
        private final JuzgadoRepository juzgadoRepository;
        private final HardwareRepository hardwareRepository;
        private final UserRepository userRepository;
        private final AuditLogRepository auditLogRepository;

        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        /**
         * Mapa de transiciones válidas de estado.
         * Cada estado tiene un conjunto de estados a los que puede transicionar.
         */
        private static final Map<TicketStatus, Set<TicketStatus>> VALID_TRANSITIONS = Map.of(
                        TicketStatus.SOLICITADO, Set.of(TicketStatus.ASIGNADO),
                        TicketStatus.ASIGNADO, Set.of(TicketStatus.EN_CURSO, TicketStatus.SOLICITADO),
                        TicketStatus.EN_CURSO, Set.of(TicketStatus.CERRADO, TicketStatus.ASIGNADO),
                        TicketStatus.CERRADO, Set.of() // Estado final — no permite transiciones
        );

        /**
         * Devuelve tickets filtrados según el rol del usuario:
         * - ADMINISTRADOR/OPERADOR: todos los tickets
         * - TECNICO: solo tickets asignados al técnico
         */
        @Transactional(readOnly = true)
        public List<TicketResponse> findAllForUser(User user) {
                if (user.getRole() == RoleName.TECNICO) {
                        return ticketRepository.findAllByTecnicoAsignadoIdAndDeletedFalse(user.getId())
                                        .stream().map(this::toResponse).toList();
                }
                return ticketRepository.findAllByDeletedFalse().stream()
                                .map(this::toResponse)
                                .toList();
        }

        /**
         * Devuelve tickets del usuario autenticado según su rol:
         * - TECNICO: tickets asignados
         * - OPERADOR: tickets que creó (solicitante)
         * - ADMINISTRADOR: todos
         */
        @Transactional(readOnly = true)
        public List<TicketResponse> findByUser(User user) {
                if (user.getRole() == RoleName.TECNICO) {
                        return ticketRepository.findAllByTecnicoAsignadoIdAndDeletedFalse(user.getId())
                                        .stream().map(this::toResponse).toList();
                }
                if (user.getRole() == RoleName.OPERADOR) {
                        return ticketRepository.findAllBySolicitanteIdAndDeletedFalse(user.getId())
                                        .stream().map(this::toResponse).toList();
                }
                // ADMINISTRADOR ve todo
                return ticketRepository.findAllByDeletedFalse().stream()
                                .map(this::toResponse).toList();
        }

        @Transactional(readOnly = true)
        public List<TicketResponse> findAll() {
                return ticketRepository.findAllByDeletedFalse().stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TicketResponse findById(Long id) {
                Ticket ticket = ticketRepository.findById(id)
                                .filter(t -> !t.getDeleted())
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
                return toResponse(ticket);
        }

        @Transactional
        public TicketResponse create(TicketRequest request, User solicitante) {
                Ticket ticket = Ticket.builder()
                                .asunto(request.getAsunto())
                                .descripcion(request.getDescripcion())
                                .canal(request.getCanal() != null ? request.getCanal() : "WEB")
                                .solicitante(solicitante)
                                .build();

                // Prioridad
                if (request.getPrioridad() != null) {
                        ticket.setPrioridad(Priority.valueOf(request.getPrioridad()));
                }

                // Juzgado
                if (request.getJuzgadoId() != null) {
                        Juzgado juzgado = juzgadoRepository.findById(request.getJuzgadoId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Juzgado",
                                                        request.getJuzgadoId()));
                        ticket.setJuzgado(juzgado);
                }

                // Hardware vinculado — con validación de ticket activo
                if (request.getHardwareId() != null) {
                        Hardware hw = hardwareRepository.findById(request.getHardwareId())
                                        .filter(h -> !h.getDeleted())
                                        .orElseThrow(() -> new ResourceNotFoundException("Hardware",
                                                        request.getHardwareId()));

                        // Regla de negocio: un equipo no puede tener más de un ticket activo
                        boolean hasActiveTicket = ticketRepository
                                        .existsByHardwareAfectadoIdAndStatusNotAndDeletedFalse(
                                                        hw.getId(), TicketStatus.CERRADO);
                        if (hasActiveTicket) {
                                throw new BusinessRuleException(
                                                "El equipo " + hw.getInventarioPatrimonial()
                                                                + " ya tiene un ticket activo. Cierre el ticket existente antes de crear uno nuevo.");
                        }

                        ticket.setHardwareAfectado(hw);
                }

                // Regla de negocio: Si el asunto contiene "juez" o "audiencia" → prioridad ALTA
                String asuntoLower = request.getAsunto().toLowerCase();
                if (asuntoLower.contains("juez") || asuntoLower.contains("audiencia")
                                || asuntoLower.contains("sala")) {
                        ticket.setPrioridad(Priority.ALTA);
                }

                Ticket saved = ticketRepository.save(ticket);

                // Registro auditoría
                auditLogRepository.save(AuditLog.builder()
                                .entityName("Ticket")
                                .entityId(saved.getId())
                                .action("CREAR")
                                .username(solicitante.getUsername())
                                .newValue("Ticket creado: " + saved.getAsunto())
                                .build());

                return toResponse(saved);
        }

        @Transactional
        public TicketResponse changeStatus(Long id, StatusChangeRequest request, String username) {
                Ticket ticket = ticketRepository.findById(id)
                                .filter(t -> !t.getDeleted())
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));

                TicketStatus oldStatus = ticket.getStatus();
                TicketStatus newStatus = TicketStatus.valueOf(request.getStatus());

                // Validar transición de estado
                Set<TicketStatus> allowedTransitions = VALID_TRANSITIONS.getOrDefault(oldStatus, Set.of());
                if (!allowedTransitions.contains(newStatus)) {
                        throw new BusinessRuleException(
                                        String.format("Transición de estado inválida: %s → %s. Transiciones permitidas desde %s: %s",
                                                        oldStatus, newStatus, oldStatus, allowedTransitions));
                }

                ticket.setStatus(newStatus);

                // Asignar técnico si se proporciona — validando que sea TECNICO
                if (request.getTecnicoId() != null) {
                        User tecnico = userRepository.findById(request.getTecnicoId())
                                        .filter(u -> !u.getDeleted())
                                        .orElseThrow(() -> new ResourceNotFoundException("Técnico",
                                                        request.getTecnicoId()));

                        if (tecnico.getRole() != RoleName.TECNICO) {
                                throw new BusinessRuleException(
                                                "El usuario '" + tecnico.getFullName()
                                                                + "' no tiene rol TECNICO. Solo se pueden asignar técnicos a los tickets.");
                        }

                        ticket.setTecnicoAsignado(tecnico);
                }

                // Setear closedAt explícitamente al cerrar
                if (newStatus == TicketStatus.CERRADO) {
                        ticket.setClosedAt(LocalDateTime.now());
                }

                // Agregar a bitácora
                if (request.getComentario() != null) {
                        String bitacoraEntry = String.format("[%s] %s: %s\n",
                                        LocalDateTime.now().format(FMT), username, request.getComentario());
                        ticket.setBitacora(
                                        (ticket.getBitacora() != null ? ticket.getBitacora() : "") + bitacoraEntry);
                }

                Ticket saved = ticketRepository.save(ticket);

                // Registro auditoría
                auditLogRepository.save(AuditLog.builder()
                                .entityName("Ticket")
                                .entityId(saved.getId())
                                .action("CAMBIO_ESTADO")
                                .username(username)
                                .field("status")
                                .oldValue(oldStatus.name())
                                .newValue(newStatus.name())
                                .build());

                return toResponse(saved);
        }

        @Transactional
        public void softDelete(Long id, String username) {
                Ticket ticket = ticketRepository.findById(id)
                                .filter(t -> !t.getDeleted())
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
                ticket.setDeleted(true);
                ticketRepository.save(ticket);

                auditLogRepository.save(AuditLog.builder()
                                .entityName("Ticket")
                                .entityId(id)
                                .action("ELIMINAR")
                                .username(username)
                                .oldValue("active")
                                .newValue("deleted")
                                .build());
        }

        private TicketResponse toResponse(Ticket t) {
                return TicketResponse.builder()
                                .id(t.getId())
                                .asunto(t.getAsunto())
                                .descripcion(t.getDescripcion())
                                .status(t.getStatus().name())
                                .prioridad(t.getPrioridad().name())
                                .juzgadoNombre(t.getJuzgado() != null ? t.getJuzgado().getNombre() : null)
                                .solicitanteNombre(t.getSolicitante() != null ? t.getSolicitante().getFullName() : null)
                                .tecnicoNombre(t.getTecnicoAsignado() != null ? t.getTecnicoAsignado().getFullName()
                                                : null)
                                .hardwareInventario(
                                                t.getHardwareAfectado() != null
                                                                ? t.getHardwareAfectado().getInventarioPatrimonial()
                                                                : null)
                                .bitacora(t.getBitacora())
                                .canal(t.getCanal())
                                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().format(FMT) : null)
                                .updatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt().format(FMT) : null)
                                .closedAt(t.getClosedAt() != null ? t.getClosedAt().format(FMT) : null)
                                .build();
        }
}
