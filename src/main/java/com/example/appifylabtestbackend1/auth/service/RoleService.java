package com.example.appifylabtestbackend1.auth.service;

import com.example.appifylabtestbackend1.auth.dto.request.role.CreateRoleRequest;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.auth.model.enitty.RoleEntity;

public interface RoleService {
    SuccessResponse createRole(CreateRoleRequest request);

    RoleEntity getRoleEntity(String name);
}
