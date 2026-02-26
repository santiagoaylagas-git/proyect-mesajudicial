package com.sojus.config;

import com.sojus.domain.entity.Circunscripcion;
import com.sojus.domain.entity.Contract;
import com.sojus.domain.entity.Distrito;
import com.sojus.domain.entity.Edificio;
import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Juzgado;
import com.sojus.domain.entity.Software;
import com.sojus.domain.entity.Ticket;
import com.sojus.domain.entity.User;
import com.sojus.domain.enums.AssetStatus;
import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.RoleName;
import com.sojus.domain.enums.TicketStatus;
import com.sojus.repository.CircunscripcionRepository;
import com.sojus.repository.ContractRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.JuzgadoRepository;
import com.sojus.repository.SoftwareRepository;
import com.sojus.repository.TicketRepository;
import com.sojus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final CircunscripcionRepository circunscripcionRepository;
        private final JuzgadoRepository juzgadoRepository;
        private final HardwareRepository hardwareRepository;
        private final SoftwareRepository softwareRepository;
        private final ContractRepository contractRepository;
        private final TicketRepository ticketRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                if (userRepository.count() > 0) {
                        log.info("Base de datos ya inicializada, saltando seed...");
                        return;
                }

                log.info("🌱 Inicializando datos de demostración...");

                // ---- Estructura Territorial ----
                Circunscripcion circ1 = circunscripcionRepository.save(
                                Circunscripcion.builder().nombre("Primera Circunscripción").codigo("CIRC-001").build());
                Circunscripcion circ2 = circunscripcionRepository.save(
                                Circunscripcion.builder().nombre("Segunda Circunscripción").codigo("CIRC-002").build());

                // Distritos y Edificios inline (JPA cascade)
                Distrito distSF = Distrito.builder().nombre("Santa Fe").ciudad("Santa Fe").circunscripcion(circ1)
                                .build();
                Edificio edifTribSF = Edificio.builder().nombre("Tribunales Santa Fe").direccion("1° de Mayo 2551")
                                .distrito(distSF).build();
                distSF.getEdificios().add(edifTribSF);
                circ1.getDistritos().add(distSF);

                Distrito distROS = Distrito.builder().nombre("Rosario").ciudad("Rosario").circunscripcion(circ2)
                                .build();
                Edificio edifTribROS = Edificio.builder().nombre("Tribunales Rosario").direccion("Balcarce 1651")
                                .distrito(distROS).build();
                distROS.getEdificios().add(edifTribROS);
                circ2.getDistritos().add(distROS);

                circ1 = circunscripcionRepository.save(circ1);
                circ2 = circunscripcionRepository.save(circ2);

                // Obtener referencias administradas después del cascade
                edifTribSF = circ1.getDistritos().get(0).getEdificios().get(0);
                edifTribROS = circ2.getDistritos().get(0).getEdificios().get(0);

                // Juzgados
                Juzgado juz1 = juzgadoRepository.save(Juzgado.builder()
                                .nombre("Juzgado Civil y Comercial Nro 1").fuero("Civil").secretaria("Secretaría 1")
                                .edificio(edifTribSF).build());
                Juzgado juz2 = juzgadoRepository.save(Juzgado.builder()
                                .nombre("Juzgado Penal Nro 3").fuero("Penal").secretaria("Secretaría 1")
                                .edificio(edifTribROS).build());
                Juzgado juz3 = juzgadoRepository.save(Juzgado.builder()
                                .nombre("Juzgado Laboral Nro 2").fuero("Laboral").secretaria("Secretaría Única")
                                .edificio(edifTribSF).build());

                // ---- Usuarios ----
                userRepository.save(User.builder()
                                .username("admin").password(passwordEncoder.encode("admin123"))
                                .fullName("María García (Admin)").email("admin@poderjudicial.gov.ar")
                                .role(RoleName.ADMINISTRADOR).juzgado(juz1).build());

                User operador = userRepository.save(User.builder()
                                .username("operador").password(passwordEncoder.encode("oper123"))
                                .fullName("Carlos López (Operador)").email("operador@poderjudicial.gov.ar")
                                .role(RoleName.OPERADOR).juzgado(juz1).build());

                User tecnico = userRepository.save(User.builder()
                                .username("tecnico").password(passwordEncoder.encode("tec123"))
                                .fullName("Ana Martínez (Técnica)").email("tecnico@poderjudicial.gov.ar")
                                .role(RoleName.TECNICO).build());

                // ---- Hardware ----
                hardwareRepository.save(Hardware.builder()
                                .inventarioPatrimonial("INV-001-0001").numeroSerie("SN-DELL-001")
                                .clase("PC").tipo("Desktop").marca("Dell").modelo("OptiPlex 7090")
                                .estado(AssetStatus.ACTIVO).juzgado(juz1).ubicacionFisica("Puesto Secretario").build());

                Hardware hw2 = hardwareRepository.save(Hardware.builder()
                                .inventarioPatrimonial("INV-001-0002").numeroSerie("SN-HP-002")
                                .clase("PC").tipo("All-in-One").marca("HP").modelo("ProOne 440 G9")
                                .estado(AssetStatus.ACTIVO).juzgado(juz2).ubicacionFisica("Puesto Juez").build());

                Hardware hw3 = hardwareRepository.save(Hardware.builder()
                                .inventarioPatrimonial("INV-002-0001").numeroSerie("SN-EPSON-001")
                                .clase("Impresora").tipo("Láser").marca("Epson").modelo("WorkForce Pro WF-C5790")
                                .estado(AssetStatus.ACTIVO).juzgado(juz1).ubicacionFisica("Mesa Compartida").build());

                hardwareRepository.save(Hardware.builder()
                                .inventarioPatrimonial("INV-003-0001").numeroSerie("SN-DELL-SRV-001")
                                .clase("Servidor").tipo("Rack").marca("Dell").modelo("PowerEdge R750")
                                .estado(AssetStatus.ACTIVO).ubicacionFisica("Data Center - Rack 3").build());

                // ---- Software ----
                softwareRepository.save(Software.builder()
                                .nombre("Microsoft Office 365").version("2024").fabricante("Microsoft")
                                .tipoLicencia("Suscripción Anual").cantidadLicencias(500)
                                .fechaVencimiento(LocalDate.of(2026, 12, 31)).build());

                softwareRepository.save(Software.builder()
                                .nombre("Antivirus ESET Endpoint").version("10.1").fabricante("ESET")
                                .tipoLicencia("Corporativa").cantidadLicencias(800)
                                .fechaVencimiento(LocalDate.of(2026, 6, 30)).build());

                softwareRepository.save(Software.builder()
                                .nombre("Sistema LEX Doctor").version("12.0").fabricante("LEX Doctor")
                                .tipoLicencia("Perpetua").cantidadLicencias(200).build());

                // ---- Contratos ----
                contractRepository.save(Contract.builder()
                                .nombre("Soporte HW Dell").proveedor("Dell Argentina S.A.")
                                .numeroContrato("CNT-2024-001")
                                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2026, 12, 31))
                                .coberturaHw("PCs y Servidores Dell")
                                .slaDescripcion("Respuesta 4hs hábiles, resolución 24hs").build());

                contractRepository.save(Contract.builder()
                                .nombre("Mantenimiento Impresoras").proveedor("Tecno Print SRL")
                                .numeroContrato("CNT-2024-002")
                                .fechaInicio(LocalDate.of(2024, 3, 1)).fechaFin(LocalDate.of(2026, 3, 15))
                                .coberturaHw("Impresoras Epson y HP").slaDescripcion("Visita técnica en 48hs").build());

                contractRepository.save(Contract.builder()
                                .nombre("Licencias Microsoft EA").proveedor("Microsoft Corp.")
                                .numeroContrato("CNT-2024-003")
                                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2026, 4, 1))
                                .coberturaSw("Office 365, Windows, Azure AD").slaDescripcion("Mesa de ayuda 24/7")
                                .build());

                // ---- Tickets ----
                ticketRepository.save(Ticket.builder()
                                .asunto("Impresora no funciona en Secretaría")
                                .descripcion("La impresora del puesto del Secretario no enciende desde ayer.")
                                .prioridad(Priority.MEDIA).juzgado(juz1).solicitante(operador)
                                .tecnicoAsignado(tecnico).status(TicketStatus.ASIGNADO)
                                .hardwareAfectado(hw3).canal("WEB")
                                .bitacora("[2026-02-25 09:00] operador: Creado el ticket\n[2026-02-25 09:15] admin: Asignado a Ana Martínez\n")
                                .build());

                ticketRepository.save(Ticket.builder()
                                .asunto("PC del Juez no inicia - Sala de Audiencias")
                                .descripcion("La PC del Juez en la Sala de Audiencias no enciende. URGENTE.")
                                .prioridad(Priority.ALTA).juzgado(juz2).solicitante(operador)
                                .status(TicketStatus.SOLICITADO)
                                .hardwareAfectado(hw2).canal("WEB").build());

                ticketRepository.save(Ticket.builder()
                                .asunto("Solicitar tóner para impresora")
                                .descripcion("Se necesita cambio de tóner en la impresora de Mesa Compartida.")
                                .prioridad(Priority.BAJA).juzgado(juz1).solicitante(operador)
                                .tecnicoAsignado(tecnico).status(TicketStatus.EN_CURSO)
                                .canal("PORTAL").build());

                ticketRepository.save(Ticket.builder()
                                .asunto("Instalación de LEX Doctor en nueva PC")
                                .descripcion("Instalar LEX Doctor 12.0 en la nueva PC del Juzgado Laboral.")
                                .prioridad(Priority.MEDIA).juzgado(juz3).solicitante(operador)
                                .tecnicoAsignado(tecnico).status(TicketStatus.CERRADO)
                                .canal("WEB")
                                .bitacora("[2026-02-20 10:00] operador: Solicitud de instalación\n[2026-02-21 14:30] tecnico: Instalación completada\n")
                                .build());

                log.info("✅ Datos de demostración cargados exitosamente");
                log.info("   👤 Usuarios de demo creados (ver documentación para credenciales)");
        }
}
