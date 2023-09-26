package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.model.Role;
import com.example.onlinebookstore.repository.role.RoleRepository;
import com.example.onlinebookstore.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a role by id " + id));
    }

    @Override
    public void deleteByID(Long id) {
        roleRepository.deleteById(id);
    }
}
