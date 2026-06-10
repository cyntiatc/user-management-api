package com.cyntia.user_management_api.service;

import com.cyntia.user_management_api.dto.AuthResponse;
import com.cyntia.user_management_api.dto.LoginRequest;
import com.cyntia.user_management_api.dto.UserRequest;
import com.cyntia.user_management_api.dto.UserResponse;

public interface AuthService {

    UserResponse register(UserRequest request);

    AuthResponse login(LoginRequest request);
}