package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.LoginRequest;
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
 * Tests de integración para el módulo de autenticación.
 * Usa el contexto Spring Boot completo con la BD H2 y datos semilla.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auth — Tests de Integración")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Login exitoso con credenciales válidas")
        void loginExitoso() throws Exception {
            LoginRequest login = new LoginRequest();
            login.setUsername("admin");
            login.setPassword("admin123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.role").value("ADMINISTRADOR"));
        }

        @Test
        @DisplayName("Login fallido con password incorrecta — 409 Conflict")
        void loginFallido_passwordIncorrecta() throws Exception {
            LoginRequest login = new LoginRequest();
            login.setUsername("admin");
            login.setPassword("wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Login fallido con usuario inexistente — 409 Conflict")
        void loginFallido_usuarioNoExiste() throws Exception {
            LoginRequest login = new LoginRequest();
            login.setUsername("noexiste");
            login.setPassword("123456");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class Me {

        @Test
        @DisplayName("Obtener usuario autenticado con token válido")
        void meConToken() throws Exception {
            String token = loginAndGetToken("admin", "admin123");

            mockMvc.perform(get("/api/auth/me")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("admin"));
        }

        @Test
        @DisplayName("Acceso sin token retorna 403")
        void meSinToken() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Control de Acceso por Roles")
    class ControlAcceso {

        @Test
        @DisplayName("ADMIN puede acceder a /api/users")
        void adminAccedeUsuarios() throws Exception {
            String token = loginAndGetToken("admin", "admin123");

            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TECNICO no puede acceder a /api/users")
        void tecnicoNoAccedeUsuarios() throws Exception {
            String token = loginAndGetToken("tecnico", "tec123");

            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("OPERADOR puede acceder a GET /api/tickets")
        void operadorAccedeTickets() throws Exception {
            String token = loginAndGetToken("operador", "oper123");

            mockMvc.perform(get("/api/tickets")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Swagger UI es accesible sin autenticación")
        void swaggerPublico() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                    .andExpect(status().isOk());
        }
    }
}
