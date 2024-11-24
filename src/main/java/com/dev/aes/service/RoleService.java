package com.dev.aes.service;

import com.dev.aes.entity.Role;

public interface RoleService {
    Role findByName(String name);
}
