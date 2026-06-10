package com.cyntia.user_management_api.service.impl;

import com.cyntia.user_management_api.config.JwtUtil;
import com.cyntia.user_management_api.dto.AuthResponse;
import com.cyntia.user_management_api.dto.LoginRequest;
import com.cyntia.user_management_api.dto.UserRequest;
import com.cyntia.user_management_api.dto.UserResponse;
import com.cyntia.user_management_api.exception.ConflictException;
import com.cyntia.user_management_api.exception.ResourceNotFoundException;
import com.cyntia.user_management_api.mapper.UserMapper;
import com.cyntia.user_management_api.model.Role;
import com.cyntia.user_management_api.model.User;
import com.cyntia.user_management_api.repository.UserRepository;
import com.cyntia.user_management_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Un utilisateur avec cet email existe déjà : " + request.email());
        }

        Set<Role> roles = (request.roles() != null && !request.roles().isEmpty())
                ? request.roles()
                : Set.of(Role.ROLE_USER);

        String encodedPassword = passwordEncoder.encode(request.password());
        return UserMapper.toResponse(userRepository.save(UserMapper.toEntity(request, encodedPassword, roles)));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + request.email()));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getNom());
    }
}