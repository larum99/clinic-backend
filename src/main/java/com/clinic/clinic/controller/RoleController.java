package com.clinic.clinic.controller;

import com.clinic.clinic.dto.response.RoleResponse;
import com.clinic.clinic.service.RoleService;
import com.clinic.clinic.utils.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Endpoints.ROLES_PATH)
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> findAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

}
