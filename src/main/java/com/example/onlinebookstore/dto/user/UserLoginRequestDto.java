package com.example.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Size(min = 6, max = 50)
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 50)
        String password
) {
}
