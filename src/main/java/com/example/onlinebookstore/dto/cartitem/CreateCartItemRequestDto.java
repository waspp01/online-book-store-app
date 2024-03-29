package com.example.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCartItemRequestDto {
    @Min(1)
    private Long bookId;

    @Min(1)
    private int quantity;
}
