package com.cyntia.user_management_api.mapper;

import com.cyntia.user_management_api.dto.UserRequest;
import com.cyntia.user_management_api.dto.UserResponse;
import com.cyntia.user_management_api.model.Role;
import com.cyntia.user_management_api.model.User;

import java.util.Set;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(user.getId(), user.getNom(), user.getEmail(), user.getRoles());
    }

    /**
     * Crée une nouvelle entité User à partir d'un DTO de création.
     * Le mot de passe doit être encodé avant d'être passé ici (responsabilité du service).
     */
    public static User toEntity(UserRequest request, String encodedPassword, Set<Role> roles) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .nom(request.nom())
                .email(request.email())
                .password(encodedPassword)
                .roles(roles)
                .build();
    }
}