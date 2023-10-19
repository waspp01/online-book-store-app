package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(Long userId,
                                   CreateCartItemRequestDto requestDto);

    ShoppingCartDto getById(Long userId);

    ShoppingCartDto updateById(Long userId, Long cartItemId, UpdateCartItemRequestDto requestDto);

    ShoppingCartDto deleteById(Long userId, Long caretItemId);
}
