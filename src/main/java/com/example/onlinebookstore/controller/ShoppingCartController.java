package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RestController
@RequestMapping(value = "/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get all info about the cart", description = "Get all info about the cart")
    @ApiResponse(responseCode = "200", description = "Cart information",
            content = {@Content(mediaType = "application/json")})
    public ShoppingCartDto getById(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getById(user.getId());
    }

    @PostMapping
    @Operation(summary = "Add cart item to the cart",
            description = "Add needed book and its quantity")

    public ShoppingCartDto addCartItem(Authentication authentication,
                                @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addCartItem(user.getId(), requestDto);
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update a cart item in a given cart",
            description = "Change the number of a book")
    @ApiResponse(responseCode = "200", description = "Updated",
            content = {@Content(mediaType = "application/json")})
    public ShoppingCartDto updateCartItem(Authentication authentication,
                                          @PathVariable Long id,
                                          @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateById(user.getId(), id, requestDto);
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete a cart item from a given cart",
            description = "Delete a cart item from a given cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCartDto deleteCartItem(Authentication authentication,
                                          @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.deleteById(user.getId(), id);
    }
}
