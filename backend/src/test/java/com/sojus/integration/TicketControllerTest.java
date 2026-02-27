package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.StatusChangeRequest;
import com.sojus.dto.TicketRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para el CRUD de Tickets.
 * Prueba el flujo completo: HTTP → Controller → Service → Repository → H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tickets — Tests de Integración")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String operadorToken;
    private String tecnicoToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = loginAndGetToken("admin", "admin123");
        operadorToken = loginAndGetToken("operador", "oper123");
        tecnicoToken = loginAndGetToken("tecnico", "tec123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Nested
    @DisplayName("GET /api/tickets")
    class ListarTickets {

        @Test
        @DisplayName("Admin puede listar todos los tickets")
        void listarComoAdmin() throws Exception {
            mockMvc.perform(get("/api/tickets")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Técnico recibe solo sus tickets asignados")
        void listarComoTecnico() throws Exception {
            mockMvc.perform(get("/api/tickets")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Sin token retorna 403")
        void listarSinToken() throws Exception {
            mockMvc.perform(get("/api/tickets"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/tickets/{id}")
    class ObtenerTicket {

        @Test
        @DisplayName("Obtener ticket existente por ID")
        void obtenerExistente() throws Exception {
            mockMvc.perform(get("/api/tickets/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.asunto").isNotEmpty())
                    .andExpect(jsonPath("$.status").isNotEmpty());
        }

        @Test
        @DisplayName("Ticket inexistente retorna 404")
        void obtenerInexistente() throws Exception {
            mockMvc.perform(get("/api/tickets/9999")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/tickets")
    class CrearTicket {

        @Test
        @DisplayName("Operador crea ticket exitosamente")
        void crearComoOperador() throws Exception {
            TicketRequest request = new TicketRequest();
            request.setAsunto("Test integración - nueva PC");
            request.setDescripcion("Solicitar nueva PC para el puesto 5");
            request.setPrioridad("MEDIA");
            request.setJuzgadoId(1L);

            mockMvc.perform(post("/api/tickets")
                    .header("Authorization", "Bearer " + operadorToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.asunto").value("Test integración - nueva PC"))
                    .andExpect(jsonPath("$.status").value("SOLICITADO"));
        }

        @Test
        @DisplayName("Técnico NO puede crear tickets (403)")
        void crearComoTecnico() throws Exception {
            TicketRequest request = new TicketRequest();
            request.setAsunto("Test sin permiso");
            request.setDescripcion("No debería funcionar");
            request.setJuzgadoId(1L);

            mockMvc.perform(post("/api/tickets")
                    .header("Authorization", "Bearer " + tecnicoToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Validación: asunto vacío retorna 400")
        void crearSinAsunto() throws Exception {
            TicketRequest request = new TicketRequest();
            request.setAsunto("");
            request.setDescripcion("Falta asunto");
            request.setJuzgadoId(1L);

            mockMvc.perform(post("/api/tickets")
                    .header("Authorization", "Bearer " + operadorToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/tickets/{id}/status")
    class CambiarEstado {

        @Test
        @DisplayName("Admin cambia estado SOLICITADO → ASIGNADO")
        void cambiarEstadoAdmin() throws Exception {
            // El ticket con ID 2 (de DataInitializer) tiene status SOLICITADO
            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("ASIGNADO");
            req.setTecnicoId(3L); // ID del técnico
            req.setComentario("Asignado desde test de integración");

            mockMvc.perform(patch("/api/tickets/2/status")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ASIGNADO"));
        }

        @Test
        @DisplayName("Operador NO puede cambiar estado (403)")
        void cambiarEstadoOperador() throws Exception {
            StatusChangeRequest req = new StatusChangeRequest();
            req.setStatus("ASIGNADO");

            mockMvc.perform(patch("/api/tickets/1/status")
                    .header("Authorization", "Bearer " + operadorToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }
    }
}
