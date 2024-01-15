package com.example.onlinebookstore.util.user;

import com.example.onlinebookstore.model.Role;
import com.example.onlinebookstore.model.User;
import java.util.Set;

public class TestUserSupplier {
    public static User getTestUser() {
        Role role = new Role();
        role.setName(Role.RoleName.USER);
        return new User()
                .setId(1L).setEmail("user1@i.ua").setPassword("123456").setRoles(Set.of(role));
    }
}
