package com.example.onlinebookstore.util.cartitem;

import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;

public class TestCartItemSupplier {
    public static CreateCartItemRequestDto getTestCreateCartItemRequestDto() {
        return new CreateCartItemRequestDto().setBookId(2L).setQuantity(2);
    }
}
