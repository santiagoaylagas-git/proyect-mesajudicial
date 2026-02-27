package com.sojus.service;

import com.sojus.domain.entity.Contract;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractService — Tests Unitarios")
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractService contractService;

    private Contract contratoActivo;
    private Contract contratoPorVencer;

    @BeforeEach
    void setUp() {
        contratoActivo = Contract.builder()
                .id(1L).nombre("Soporte HW Dell").proveedor("Dell Argentina")
                .numeroContrato("CNT-2024-001")
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2027, 12, 31))
                .coberturaHw("PCs y Servidores Dell")
                .slaDescripcion("Respuesta 4hs hábiles")
                .active(true).createdAt(LocalDateTime.now())
                .build();

        contratoPorVencer = Contract.builder()
                .id(2L).nombre("Licencias Microsoft").proveedor("Microsoft")
                .fechaFin(LocalDate.now().plusDays(15))
                .active(true).createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("CRUD de Contratos")
    class CrudContratos {

        @Test
        @DisplayName("findAll devuelve solo contratos activos")
        void findAll_activos() {
            when(contractRepository.findAllByActiveTrue()).thenReturn(List.of(contratoActivo));

            List<Contract> result = contractService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Soporte HW Dell");
        }

        @Test
        @DisplayName("findById devuelve contrato activo")
        void findById_exitoso() {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));

            Contract result = contractService.findById(1L);

            assertThat(result.getNombre()).isEqualTo("Soporte HW Dell");
        }

        @Test
        @DisplayName("findById de contrato inactivo lanza excepción")
        void findById_inactivo() {
            Contract inactivo = Contract.builder().id(3L).active(false).build();
            when(contractRepository.findById(3L)).thenReturn(Optional.of(inactivo));

            assertThatThrownBy(() -> contractService.findById(3L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Debe crear contrato exitosamente")
        void crear_exitoso() {
            Contract nuevo = Contract.builder()
                    .nombre("Nuevo Contrato").proveedor("Proveedor X").build();

            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
                Contract c = inv.getArgument(0);
                c.setId(10L);
                return c;
            });

            Contract result = contractService.create(nuevo);

            assertThat(result.getId()).isEqualTo(10L);
            verify(contractRepository).save(nuevo);
        }

        @Test
        @DisplayName("Debe actualizar campos del contrato")
        void actualizar_exitoso() {
            Contract updated = Contract.builder()
                    .nombre("Soporte HW Dell - Renovado")
                    .proveedor("Dell Argentina S.A.")
                    .fechaFin(LocalDate.of(2028, 12, 31))
                    .build();

            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));
            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

            Contract result = contractService.update(1L, updated);

            assertThat(result.getNombre()).isEqualTo("Soporte HW Dell - Renovado");
            assertThat(result.getFechaFin()).isEqualTo(LocalDate.of(2028, 12, 31));
        }
    }

    @Nested
    @DisplayName("Desactivación y Vencimiento")
    class DesactivacionVencimiento {

        @Test
        @DisplayName("deactivate marca contrato como inactivo")
        void desactivar_exitoso() {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));
            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

            contractService.deactivate(1L);

            assertThat(contratoActivo.getActive()).isFalse();
        }

        @Test
        @DisplayName("findExpiringSoon devuelve contratos próximos a vencer")
        void findExpiringSoon() {
            when(contractRepository.findExpiringBefore(any(LocalDate.class)))
                    .thenReturn(List.of(contratoPorVencer));

            List<Contract> result = contractService.findExpiringSoon(30);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Licencias Microsoft");
        }

        @Test
        @DisplayName("findExpiringSoon con 0 días no devuelve contratos futuros")
        void findExpiringSoon_sinResultados() {
            when(contractRepository.findExpiringBefore(any(LocalDate.class)))
                    .thenReturn(List.of());

            List<Contract> result = contractService.findExpiringSoon(0);

            assertThat(result).isEmpty();
        }
    }
}
