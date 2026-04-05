package com.example.appifylabtestbackend1.auth.model.mapper;

import com.example.appifylabtestbackend1.auth.dto.request.registration.RegistrationRequest;
import com.example.appifylabtestbackend1.auth.model.enitty.RoleEntity;
import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserMapper {
    public static UserEntity fromRequest(RegistrationRequest request,
                                         RoleEntity roleEntity,
                                         PasswordEncoder passwordEncoder) {
        UserEntity userEntity = new UserEntity();
        userEntity.setRoleEntity(roleEntity);
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setUsername(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        return userEntity;
    }
}
