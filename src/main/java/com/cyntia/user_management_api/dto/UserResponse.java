package com.cyntia.user_management_api.dto;

import com.cyntia.user_management_api.model.Role;

import java.util.Set;

public record UserResponse(
        Long id,
        String nom,
        String email,
        Set<Role> roles
) {}