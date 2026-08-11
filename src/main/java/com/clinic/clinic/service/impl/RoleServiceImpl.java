package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.response.RoleResponse;
import com.clinic.clinic.repository.RoleRepository;
import com.clinic.clinic.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> findAllRoles() {

        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getName(),
                        role.getDescription()
                ))
                .toList();
    }

}
