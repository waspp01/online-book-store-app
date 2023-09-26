package com.example.onlinebookstore.dto.user;

import com.example.onlinebookstore.lib.FieldsValueMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@FieldsValueMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!"
)
@Data
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    @Size(min = 6, max = 50)
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank
    @Size(min = 6, max = 50)
    private String repeatPassword;

    @NotBlank
    @Size(min = 2, max = 60)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;

    private String shippingAddress;
}
