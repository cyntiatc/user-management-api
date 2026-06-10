package com.cyntia.user_management_api.dto;

public record AuthResponse(
        String token,
        String type,
        String email,
        String nom
) {
    public AuthResponse(String token, String email, String nom) {
        this(token, "Bearer", email, nom);
    }
}