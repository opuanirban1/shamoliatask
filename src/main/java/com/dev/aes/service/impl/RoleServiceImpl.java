package com.dev.aes.service.impl;

import com.dev.aes.entity.Role;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.repository.RoleRepository;
import com.dev.aes.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new OcrDmsException("Role Not Found", HttpStatus.BAD_REQUEST));
    }
}
