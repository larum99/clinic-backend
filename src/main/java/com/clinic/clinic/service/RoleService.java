package com.clinic.clinic.service;

import com.clinic.clinic.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> findAllRoles();

}
