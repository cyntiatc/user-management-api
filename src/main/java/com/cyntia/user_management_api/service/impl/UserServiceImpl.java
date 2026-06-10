package com.cyntia.user_management_api.service.impl;

import com.cyntia.user_management_api.dto.MessageResponse;
import com.cyntia.user_management_api.dto.UserRequest;
import com.cyntia.user_management_api.dto.UserResponse;
import com.cyntia.user_management_api.exception.ResourceNotFoundException;
import com.cyntia.user_management_api.mapper.UserMapper;
import com.cyntia.user_management_api.model.User;
import com.cyntia.user_management_api.repository.UserRepository;
import com.cyntia.user_management_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        user.setNom(request.nom());
        user.setEmail(request.email());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(request.roles());
        }

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public MessageResponse deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id);
        }
        userRepository.deleteById(id);
        return new MessageResponse("Utilisateur supprimé avec succès.");
    }
}