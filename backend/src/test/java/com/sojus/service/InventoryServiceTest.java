package com.sojus.service;

import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Software;
import com.sojus.domain.enums.AssetStatus;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.SoftwareRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService — Tests Unitarios")
class InventoryServiceTest {

    @Mock
    private HardwareRepository hardwareRepository;
    @Mock
    private SoftwareRepository softwareRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Hardware pc;
    private Software office;

    @BeforeEach
    void setUp() {
        pc = Hardware.builder()
                .id(1L).inventarioPatrimonial("INV-001-0001").numeroSerie("SN-001")
                .clase("PC").tipo("Desktop").marca("Dell").modelo("OptiPlex 7090")
                .estado(AssetStatus.ACTIVO).ubicacionFisica("Puesto 1")
                .deleted(false).createdAt(LocalDateTime.now())
                .build();

        office = Software.builder()
                .id(1L).nombre("Microsoft Office 365").version("2024")
                .fabricante("Microsoft").tipoLicencia("Suscripción")
                .cantidadLicencias(500).deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // HARDWARE
    // ================================================================
    @Nested
    @DisplayName("CRUD de Hardware")
    class CrudHardware {

        @Test
        @DisplayName("findAllHardware devuelve solo no eliminados")
        void findAll() {
            when(hardwareRepository.findAllByDeletedFalse()).thenReturn(List.of(pc));

            List<Hardware> result = inventoryService.findAllHardware();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInventarioPatrimonial()).isEqualTo("INV-001-0001");
        }

        @Test
        @DisplayName("findHardwareById devuelve hardware existente")
        void findById_exitoso() {
            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));

            Hardware result = inventoryService.findHardwareById(1L);

            assertThat(result.getMarca()).isEqualTo("Dell");
        }

        @Test
        @DisplayName("findHardwareById de eliminado lanza excepción")
        void findById_eliminado() {
            Hardware deleted = Hardware.builder().id(2L).deleted(true).build();
            when(hardwareRepository.findById(2L)).thenReturn(Optional.of(deleted));

            assertThatThrownBy(() -> inventoryService.findHardwareById(2L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("createHardware exitoso")
        void crear_exitoso() {
            Hardware nuevo = Hardware.builder()
                    .inventarioPatrimonial("INV-NEW-0001").clase("Impresora").build();

            when(hardwareRepository.existsByInventarioPatrimonial("INV-NEW-0001")).thenReturn(false);
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> {
                Hardware h = inv.getArgument(0);
                h.setId(10L);
                return h;
            });

            Hardware result = inventoryService.createHardware(nuevo);

            assertThat(result.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("createHardware rechaza inventario patrimonial duplicado")
        void crear_duplicado() {
            Hardware duplicado = Hardware.builder()
                    .inventarioPatrimonial("INV-001-0001").clase("PC").build();

            when(hardwareRepository.existsByInventarioPatrimonial("INV-001-0001")).thenReturn(true);

            assertThatThrownBy(() -> inventoryService.createHardware(duplicado))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Inventario Patrimonial");
        }

        @Test
        @DisplayName("updateHardware actualiza campos correctamente")
        void actualizar_exitoso() {
            Hardware updated = Hardware.builder()
                    .clase("Servidor").tipo("Rack").marca("HP").modelo("ProLiant")
                    .estado(AssetStatus.EN_REPARACION).build();

            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> inv.getArgument(0));

            Hardware result = inventoryService.updateHardware(1L, updated);

            assertThat(result.getClase()).isEqualTo("Servidor");
            assertThat(result.getEstado()).isEqualTo(AssetStatus.EN_REPARACION);
        }

        @Test
        @DisplayName("softDeleteHardware marca como eliminado")
        void softDelete_exitoso() {
            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> inv.getArgument(0));

            inventoryService.softDeleteHardware(1L);

            assertThat(pc.getDeleted()).isTrue();
        }
    }

    // ================================================================
    // SOFTWARE
    // ================================================================
    @Nested
    @DisplayName("CRUD de Software")
    class CrudSoftware {

        @Test
        @DisplayName("findAllSoftware devuelve solo no eliminados")
        void findAll() {
            when(softwareRepository.findAllByDeletedFalse()).thenReturn(List.of(office));

            List<Software> result = inventoryService.findAllSoftware();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("createSoftware exitoso")
        void crear_exitoso() {
            Software nuevo = Software.builder().nombre("Antivirus").build();

            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> {
                Software s = inv.getArgument(0);
                s.setId(10L);
                return s;
            });

            Software result = inventoryService.createSoftware(nuevo);

            assertThat(result.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("updateSoftware actualiza campos correctamente")
        void actualizar_exitoso() {
            Software updated = Software.builder()
                    .nombre("Microsoft Office 365 - Renovado").version("2025")
                    .fabricante("Microsoft").tipoLicencia("Suscripción Anual")
                    .cantidadLicencias(600).build();

            when(softwareRepository.findById(1L)).thenReturn(Optional.of(office));
            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> inv.getArgument(0));

            Software result = inventoryService.updateSoftware(1L, updated);

            assertThat(result.getNombre()).isEqualTo("Microsoft Office 365 - Renovado");
            assertThat(result.getCantidadLicencias()).isEqualTo(600);
        }

        @Test
        @DisplayName("softDeleteSoftware marca como eliminado")
        void softDelete_exitoso() {
            when(softwareRepository.findById(1L)).thenReturn(Optional.of(office));
            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> inv.getArgument(0));

            inventoryService.softDeleteSoftware(1L);

            assertThat(office.getDeleted()).isTrue();
        }

        @Test
        @DisplayName("findSoftwareById de eliminado lanza excepción")
        void findById_eliminado() {
            Software deleted = Software.builder().id(5L).deleted(true).build();
            when(softwareRepository.findById(5L)).thenReturn(Optional.of(deleted));

            assertThatThrownBy(() -> inventoryService.findSoftwareById(5L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
