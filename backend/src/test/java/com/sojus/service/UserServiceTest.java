package com.sojus.service;

import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.dto.UserResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — Tests Unitarios")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User tecnicoUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L).username("admin").password("encoded").fullName("Admin General")
                .email("admin@test.com").role(RoleName.ADMINISTRADOR)
                .active(true).deleted(false).createdAt(LocalDateTime.now())
                .build();

        tecnicoUser = User.builder()
                .id(2L).username("tecnico").password("encoded").fullName("Juan Pérez")
                .email("tecnico@test.com").role(RoleName.TECNICO)
                .active(true).deleted(false).createdAt(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // CREAR USUARIO
    // ================================================================
    @Nested
    @DisplayName("Crear Usuarios")
    class CrearUsuarios {

        @Test
        @DisplayName("Debe crear usuario con password encodeada")
        void crearUsuario_exitoso() {
            User newUser = User.builder()
                    .username("nuevo").password("plain123").fullName("Nuevo User")
                    .role(RoleName.OPERADOR).build();

            when(userRepository.existsByUsername("nuevo")).thenReturn(false);
            when(passwordEncoder.encode("plain123")).thenReturn("$2a$encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(10L);
                u.setCreatedAt(LocalDateTime.now());
                return u;
            });

            User result = userService.create(newUser);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getPassword()).isEqualTo("$2a$encoded");
            verify(passwordEncoder).encode("plain123");
        }

        @Test
        @DisplayName("Debe rechazar username duplicado")
        void crearUsuario_usernameDuplicado() {
            User newUser = User.builder()
                    .username("admin").password("pass").fullName("Duplicado")
                    .role(RoleName.OPERADOR).build();

            when(userRepository.existsByUsername("admin")).thenReturn(true);

            assertThatThrownBy(() -> userService.create(newUser))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("ya existe");
        }
    }

    // ================================================================
    // ACTUALIZAR USUARIO
    // ================================================================
    @Nested
    @DisplayName("Actualizar Usuarios")
    class ActualizarUsuarios {

        @Test
        @DisplayName("Debe actualizar campos del usuario")
        void actualizarUsuario_exitoso() {
            User updated = User.builder()
                    .fullName("Admin Actualizado").email("new@test.com")
                    .role(RoleName.ADMINISTRADOR).active(true).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.update(1L, updated);

            assertThat(result.getFullName()).isEqualTo("Admin Actualizado");
            assertThat(result.getEmail()).isEqualTo("new@test.com");
        }

        @Test
        @DisplayName("Debe encodear nueva password si se proporciona")
        void actualizarUsuario_nuevaPassword() {
            User updated = User.builder()
                    .fullName("Admin").email("admin@test.com")
                    .role(RoleName.ADMINISTRADOR).active(true)
                    .password("newpass").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.encode("newpass")).thenReturn("$2a$newencoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.update(1L, updated);

            assertThat(result.getPassword()).isEqualTo("$2a$newencoded");
        }

        @Test
        @DisplayName("No debe encodear si password es null o vacía")
        void actualizarUsuario_sinPassword() {
            User updated = User.builder()
                    .fullName("Admin").email("admin@test.com")
                    .role(RoleName.ADMINISTRADOR).active(true)
                    .password("").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.update(1L, updated);

            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    // ================================================================
    // SOFT DELETE
    // ================================================================
    @Nested
    @DisplayName("Soft Delete de Usuarios")
    class SoftDeleteUsuarios {

        @Test
        @DisplayName("Debe marcar usuario como eliminado y desactivado")
        void softDelete_exitoso() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(tecnicoUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.softDelete(2L);

            assertThat(tecnicoUser.getDeleted()).isTrue();
            assertThat(tecnicoUser.getActive()).isFalse();
        }

        @Test
        @DisplayName("Soft delete de usuario inexistente lanza excepción")
        void softDelete_noExiste() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.softDelete(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ================================================================
    // CONSULTAS
    // ================================================================
    @Nested
    @DisplayName("Consultas de Usuarios")
    class ConsultasUsuarios {

        @Test
        @DisplayName("findAll devuelve solo usuarios no eliminados")
        void findAll_noEliminados() {
            when(userRepository.findAllByDeletedFalse()).thenReturn(List.of(adminUser, tecnicoUser));

            List<User> result = userService.findAll();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("findByRole devuelve usuarios filtrados")
        void findByRole() {
            when(userRepository.findAllByRoleAndDeletedFalse(RoleName.TECNICO))
                    .thenReturn(List.of(tecnicoUser));

            List<User> result = userService.findByRole(RoleName.TECNICO);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRole()).isEqualTo(RoleName.TECNICO);
        }

        @Test
        @DisplayName("findById de usuario eliminado lanza excepción")
        void findById_eliminado() {
            User deletedUser = User.builder().id(5L).deleted(true).build();
            when(userRepository.findById(5L)).thenReturn(Optional.of(deletedUser));

            assertThatThrownBy(() -> userService.findById(5L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("findAllAsDto devuelve UserResponse correctos")
        void findAllAsDto() {
            when(userRepository.findAllByDeletedFalse()).thenReturn(List.of(adminUser));

            List<UserResponse> result = userService.findAllAsDto();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("admin");
            assertThat(result.get(0).getRole()).isEqualTo("ADMINISTRADOR");
        }
    }
}
