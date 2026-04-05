package com.example.appifylabtestbackend1.auth.service;

import com.example.appifylabtestbackend1.auth.dto.request.registration.RegistrationRequest;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    SuccessResponse registerAdmin(RegistrationRequest request);

    SuccessResponse registerUser(RegistrationRequest request);

    UserEntity getUserById(Long id);

    SuccessResponse activateUser(UserEntity userEntity);

    Boolean existsSuperAdmin(String username);

    UserEntity getAuthenticatedUserEntity();

    UserEntity getUserByUsername(String username);
}
