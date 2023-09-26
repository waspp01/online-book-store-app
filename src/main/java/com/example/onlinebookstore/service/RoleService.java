package com.example.onlinebookstore.service;

import com.example.onlinebookstore.model.Role;

public interface RoleService {

    Role save(Role role);

    Role findById(Long id);

    void deleteByID(Long id);
}
