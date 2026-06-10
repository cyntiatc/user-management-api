package com.cyntia.user_management_api.exception;

public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String role) {
        super("Le rôle '" + role + "' est invalide ou n'existe pas.");
    }
}