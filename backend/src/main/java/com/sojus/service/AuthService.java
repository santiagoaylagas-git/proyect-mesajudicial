package com.sojus.service;

import com.sojus.domain.entity.User;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.LoginResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.repository.UserRepository;
import com.sojus.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndDeletedFalse(request.getUsername())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        if (!user.getActive()) {
            throw new BusinessRuleException("Usuario desactivado");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
