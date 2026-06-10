package com.cyntia.user_management_api.service;

import com.cyntia.user_management_api.dto.MessageResponse;
import com.cyntia.user_management_api.dto.UserRequest;
import com.cyntia.user_management_api.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    MessageResponse deleteUser(Long id);
}