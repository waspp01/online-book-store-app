package com.example.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCartItemRequestDto {
    @NotBlank
    @Min(1)
    private int quantity;
}
