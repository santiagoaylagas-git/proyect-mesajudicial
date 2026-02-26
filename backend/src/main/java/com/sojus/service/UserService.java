package com.sojus.service;

import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.dto.UserResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ---- Métodos internos (devuelven entidades) ----

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(RoleName role) {
        return userRepository.findAllByRoleAndDeletedFalse(role);
    }

    @Transactional
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessRuleException("El nombre de usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setRole(updated.getRole());
        existing.setActive(updated.getActive());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return userRepository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        User user = findById(id);
        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
    }

    // ---- Métodos DTO (para controllers) ----

    @Transactional(readOnly = true)
    public List<UserResponse> findAllAsDto() {
        return findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findByIdAsDto(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByRoleAsDto(RoleName role) {
        return findByRole(role).stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponse createAsDto(User user) {
        return toResponse(create(user));
    }

    @Transactional
    public UserResponse updateAsDto(Long id, User user) {
        return toResponse(update(id, user));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .juzgadoNombre(u.getJuzgado() != null ? u.getJuzgado().getNombre() : null)
                .active(u.getActive())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : null)
                .build();
    }
}
