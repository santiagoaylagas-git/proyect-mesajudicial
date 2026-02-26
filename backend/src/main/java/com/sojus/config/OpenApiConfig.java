package com.sojus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI sojusOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("MESA-001 — Sistema de Mesa de Ayuda — Poder Judicial")
                                                .description("Plataforma centralizada para registrar, seguir y resolver requerimientos técnicos, "
                                                                + "controlar el inventario de hardware y software, y gestionar contratos con proveedores "
                                                                + "externos, organizado por la estructura territorial del Poder Judicial.")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Santiago Aylagas — Dirección de Informática")
                                                                .email("soporte@poderjudicial.gov.ar")))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                                .schemaRequirement("Bearer JWT",
                                                new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Ingresar token JWT obtenido del endpoint /api/auth/login"));
        }
}
