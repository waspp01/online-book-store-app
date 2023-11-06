package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;

public interface CartItemService {
    CartItemDto save(CreateCartItemRequestDto requestDto);

}
