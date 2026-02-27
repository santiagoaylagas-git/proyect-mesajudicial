package com.sojus.service;

import com.sojus.domain.entity.*;
import com.sojus.domain.enums.*;
import com.sojus.dto.StatusChangeRequest;
import com.sojus.dto.TicketRequest;
import com.sojus.dto.TicketResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService — Tests Unitarios")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private JuzgadoRepository juzgadoRepository;
    @Mock
    private HardwareRepository hardwareRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private TicketService ticketService;

    private User admin;
    private User operador;
    private User tecnico;
    private Juzgado juzgado;
    private Hardware hardware;

    @BeforeEach
    void setUp() {
        juzgado = Juzgado.builder().id(1L).nombre("Juzgado Civil N°1").build();

        admin = User.builder().id(1L).username("admin").fullName("Admin")
                .role(RoleName.ADMINISTRADOR).build();
        operador = User.builder().id(2L).username("operador").fullName("Operador")
                .role(RoleName.OPERADOR).juzgado(juzgado).build();
        tecnico = User.builder().id(3L).username("tecnico").fullName("Técnico")
                .role(RoleName.TECNICO).build();

        hardware = Hardware.builder().id(1L).inventarioPatrimonial("INV-001-0001")
                .clase("PC").deleted(false).build();
    }

    // ================================================================
    // CREAR TICKETS
    // ================================================================
    @Nested
    @DisplayName("Crear Tickets")
    class CrearTickets {

        @Test
        @DisplayName("Debe crear ticket con datos válidos")
        void crearTicket_datosValidos() {
            TicketRequest request = new TicketRequest();
            request.setAsunto("PC no enciende");
            request.setDescripcion("La PC del puesto 1 no enciende");
            request.setPrioridad("MEDIA");
            request.setJuzgadoId(1L);

            when(juzgadoRepository.findById(1L)).thenReturn(Optional.of(juzgado));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
                Ticket t = inv.getArgument(0);
                t.setId(1L);
                t.setCreatedAt(LocalDateTime.now());
                return t;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.create(request, operador);

            assertThat(response).isNotNull();
            assertThat(response.getAsunto()).isEqualTo("PC no enciende");
            assertThat(response.getStatus()).isEqualTo("SOLICITADO");
            assertThat(response.getPrioridad()).isEqualTo("MEDIA");
            verify(ticketRepository).save(any(Ticket.class));
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Debe asignar prioridad ALTA si el asunto contiene 'juez'")
        void crearTicket_prioridadAltaJuez() {
            TicketRequest request = new TicketRequest();
            request.setAsunto("PC del Juez no enciende");
            request.setDescripcion("Urgente");
            request.setPrioridad("BAJA");
            request.setJuzgadoId(1L);

            when(juzgadoRepository.findById(1L)).thenReturn(Optional.of(juzgado));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
                Ticket t = inv.getArgument(0);
                t.setId(2L);
                t.setCreatedAt(LocalDateTime.now());
                return t;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.create(request, operador);

            assertThat(response.getPrioridad()).isEqualTo("ALTA");
        }

        @Test
        @DisplayName("Debe asignar prioridad ALTA si el asunto contiene 'audiencia'")
        void crearTicket_prioridadAltaAudiencia() {
            TicketRequest request = new TicketRequest();
            request.setAsunto("Sala de audiencia sin sistema");
            request.setDescripcion("No funciona");
            request.setJuzgadoId(1L);

            when(juzgadoRepository.findById(1L)).thenReturn(Optional.of(juzgado));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
                Ticket t = inv.getArgument(0);
                t.setId(3L);
                t.setCreatedAt(LocalDateTime.now());
                return t;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.create(request, operador);

            assertThat(response.getPrioridad()).isEqualTo("ALTA");
        }

        @Test
        @DisplayName("Debe rechazar si el hardware ya tiene ticket activo")
        void crearTicket_hardwareConTicketActivo() {
            TicketRequest request = new TicketRequest();
            request.setAsunto("Impresora rota");
            request.setDescripcion("No imprime");
            request.setJuzgadoId(1L);
            request.setHardwareId(1L);

            when(juzgadoRepository.findById(1L)).thenReturn(Optional.of(juzgado));
            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(hardware));
            when(ticketRepository.existsByHardwareAfectadoIdAndStatusNotAndDeletedFalse(
                    1L, TicketStatus.CERRADO)).thenReturn(true);

            assertThatThrownBy(() -> ticketService.create(request, operador))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("ya tiene un ticket activo");
        }

        @Test
        @DisplayName("Debe fallar si el juzgado no existe")
        void crearTicket_juzgadoNoExiste() {
            TicketRequest request = new TicketRequest();
            request.setAsunto("Test");
            request.setDescripcion("Desc");
            request.setJuzgadoId(999L);

            when(juzgadoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.create(request, operador))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ================================================================
    // CAMBIAR ESTADO
    // ================================================================
    @Nested
    @DisplayName("Cambiar Estado de Tickets")
    class CambiarEstado {

        @Test
        @DisplayName("SOLICITADO → ASIGNADO es válido")
        void cambioEstado_solicitadoAAsignado() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.SOLICITADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("ASIGNADO");
            req.setTecnicoId(3L);
            req.setComentario("Asignado a técnico");

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(userRepository.findById(3L)).thenReturn(Optional.of(tecnico));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.changeStatus(1L, req, "admin");

            assertThat(response.getStatus()).isEqualTo("ASIGNADO");
        }

        @Test
        @DisplayName("ASIGNADO → EN_CURSO es válido")
        void cambioEstado_asignadoAEnCurso() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.ASIGNADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("EN_CURSO");

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.changeStatus(1L, req, "tecnico");

            assertThat(response.getStatus()).isEqualTo("EN_CURSO");
        }

        @Test
        @DisplayName("EN_CURSO → CERRADO es válido y setea closedAt")
        void cambioEstado_enCursoACerrado() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.EN_CURSO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("CERRADO");
            req.setComentario("Resuelto");

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            TicketResponse response = ticketService.changeStatus(1L, req, "tecnico");

            assertThat(response.getStatus()).isEqualTo("CERRADO");
            assertThat(response.getClosedAt()).isNotNull();
        }

        @Test
        @DisplayName("SOLICITADO → EN_CURSO debe fallar (transición inválida)")
        void cambioEstado_transicionInvalida() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.SOLICITADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("EN_CURSO");

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            assertThatThrownBy(() -> ticketService.changeStatus(1L, req, "tecnico"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Transición de estado inválida");
        }

        @Test
        @DisplayName("CERRADO → cualquier estado debe fallar")
        void cambioEstado_cerradoNoPermiteTransiciones() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.CERRADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("SOLICITADO");

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            assertThatThrownBy(() -> ticketService.changeStatus(1L, req, "admin"))
                    .isInstanceOf(BusinessRuleException.class);
        }

        @Test
        @DisplayName("Debe rechazar asignación a usuario sin rol TECNICO")
        void cambioEstado_asignarNoTecnico() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").status(TicketStatus.SOLICITADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("ASIGNADO");
            req.setTecnicoId(2L);

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(userRepository.findById(2L)).thenReturn(Optional.of(operador));

            assertThatThrownBy(() -> ticketService.changeStatus(1L, req, "admin"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("no tiene rol TECNICO");
        }
    }

    // ================================================================
    // CONSULTAS
    // ================================================================
    @Nested
    @DisplayName("Consultas de Tickets")
    class Consultas {

        @Test
        @DisplayName("findAllForUser ADMIN devuelve todos los tickets")
        void findAllForUser_admin() {
            Ticket t1 = Ticket.builder().id(1L).asunto("T1").status(TicketStatus.SOLICITADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();
            Ticket t2 = Ticket.builder().id(2L).asunto("T2").status(TicketStatus.ASIGNADO)
                    .prioridad(Priority.ALTA).deleted(false).createdAt(LocalDateTime.now()).build();

            when(ticketRepository.findAllByDeletedFalse()).thenReturn(List.of(t1, t2));

            List<TicketResponse> result = ticketService.findAllForUser(admin);

            assertThat(result).hasSize(2);
            verify(ticketRepository).findAllByDeletedFalse();
        }

        @Test
        @DisplayName("findAllForUser TECNICO devuelve solo tickets asignados")
        void findAllForUser_tecnico() {
            Ticket t1 = Ticket.builder().id(1L).asunto("T1").status(TicketStatus.ASIGNADO)
                    .prioridad(Priority.MEDIA).deleted(false).createdAt(LocalDateTime.now()).build();

            when(ticketRepository.findAllByTecnicoAsignadoIdAndDeletedFalse(3L))
                    .thenReturn(List.of(t1));

            List<TicketResponse> result = ticketService.findAllForUser(tecnico);

            assertThat(result).hasSize(1);
            verify(ticketRepository).findAllByTecnicoAsignadoIdAndDeletedFalse(3L);
        }

        @Test
        @DisplayName("findById con ticket eliminado lanza excepción")
        void findById_ticketEliminado() {
            Ticket deleted = Ticket.builder().id(5L).asunto("Del").deleted(true).build();
            when(ticketRepository.findById(5L)).thenReturn(Optional.of(deleted));

            assertThatThrownBy(() -> ticketService.findById(5L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ================================================================
    // SOFT DELETE
    // ================================================================
    @Nested
    @DisplayName("Soft Delete de Tickets")
    class SoftDelete {

        @Test
        @DisplayName("Debe marcar ticket como eliminado")
        void softDelete_exitoso() {
            Ticket ticket = Ticket.builder().id(1L).asunto("Test").deleted(false)
                    .status(TicketStatus.SOLICITADO).prioridad(Priority.MEDIA)
                    .createdAt(LocalDateTime.now()).build();

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            ticketService.softDelete(1L, "admin");

            assertThat(ticket.getDeleted()).isTrue();
            verify(ticketRepository).save(ticket);
        }

        @Test
        @DisplayName("Soft delete de ticket inexistente lanza excepción")
        void softDelete_noExiste() {
            when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.softDelete(999L, "admin"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
